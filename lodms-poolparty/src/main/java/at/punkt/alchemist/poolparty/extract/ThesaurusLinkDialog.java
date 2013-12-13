package at.punkt.alchemist.poolparty.extract;

import at.punkt.alchemist.poolparty.PoolPartyApiConfig;
import at.punkt.alchemist.poolparty.PoolPartyApiPanel;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.poolparty.api.Authentication;
import at.punkt.poolparty.api.PPTApi;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Window.Notification;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 *
 * @author kreisera
 */
public class ThesaurusLinkDialog extends VerticalLayout implements ConfigDialog {

    private final Logger logger = Logger.getLogger(ThesaurusLinkDialog.class);
    private final ThesaurusLinkConfig config;
    private final ComboBox linkProperty = new ComboBox("Linking Property");
    private PoolPartyApiPanel apiPanel;

    public class LinkingProperty {

        String uri;
        String label;

        public LinkingProperty(String uri, String label) {
            this.uri = uri;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }

    ThesaurusLinkDialog(ThesaurusLinkConfig config) {
        this.config = config;
        apiPanel = new PoolPartyApiPanel(config.getApiConfig());
        addComponent(apiPanel);
        
        LinkingProperty exactMatch = new LinkingProperty("http://www.w3.org/2004/02/skos/core#exactMatch", "skos:exactMatch");
        linkProperty.setContainerDataSource(new BeanItemContainer<LinkingProperty>(LinkingProperty.class,
                                                                                   Arrays.asList(exactMatch,
                                                                                                 new LinkingProperty("http://www.w3.org/2004/02/skos/core#closeMatch", "skos:closeMatch"),
                                                                                                 new LinkingProperty("http://www.w3.org/2004/02/skos/core#relatedMatch", "skos:relatedMatch"),
                                                                                                 new LinkingProperty("http://www.w3.org/2004/02/skos/core#broadMatch", "skos:broadMatch"),
                                                                                                 new LinkingProperty("http://www.w3.org/2004/02/skos/core#narrowerMatch", "skos:narrowerMatch"),
                                                                                                 new LinkingProperty("http://www.w3.org/2002/07/owl#sameAs", "owl:sameAs"),
                                                                                                 new LinkingProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso", "rdfs:seeAlso"))));
        linkProperty.setNullSelectionAllowed(false);
        linkProperty.setRequired(true);
        linkProperty.setRequiredError("Linking property is required");
        linkProperty.setItemCaptionPropertyId("label");
        linkProperty.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        for (Object p : linkProperty.getItemIds()) {
            if (((LinkingProperty) p).getUri().equals(config.getLinkProperty())) {
                linkProperty.setValue(p);
            }
        }
        addComponent(linkProperty);
    }

    @Override
    public Object getConfig() {
        try {
            PoolPartyApiConfig apiConfig = apiPanel.getApiConfig();
            URL url = PPTApi.getServiceUrl(apiConfig.getServer(), "PoolParty/sparql/" + apiConfig.getUriSupplement()+ "?query=" + URLEncoder.encode("ASK {?x a <http://www.w3.org/2004/02/skos/core#Concept> }", "UTF-8"));
            logger.info(url);
            Authentication authentication = apiConfig.getAuthentication();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            authentication.visit(con);
            if (con.getResponseCode() != 200) {
                getWindow().showNotification("Unable to query SPARQL endpoint of project", "", Notification.TYPE_ERROR_MESSAGE);
                throw new RuntimeException("Response code: "+con.getResponseCode());
            }
            config.setApiConfig(apiConfig);
            config.setLinkProperty(((LinkingProperty) linkProperty.getValue()).getUri());
        } catch (Exception ex) {
            logger.error("Unable to query SPARQL endpoint of project", ex);
        }
        return config;
    }
}