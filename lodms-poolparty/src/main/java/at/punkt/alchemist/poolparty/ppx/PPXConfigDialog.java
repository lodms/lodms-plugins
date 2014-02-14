/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.alchemist.poolparty.ppx;

import at.punkt.alchemist.poolparty.PoolPartyApiConfig;
import at.punkt.alchemist.poolparty.PoolPartyApiPanel;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.poolparty.api.Authentication;
import at.punkt.poolparty.api.PPTApi;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.log4j.Logger;

/**
 *
 * @author Kata
 */
public class PPXConfigDialog extends VerticalLayout implements ConfigDialog {

    private final PPXConfig config;
    private PoolPartyApiPanel apiPanel;
    private final TextField numberOfConcepts = new TextField("Number of concepts");
    private final TextField numberOfTerms = new TextField("Number of terms");
    private final ComboBox language = new ComboBox("Language");
    private final CheckBox transitiveBroader = new CheckBox("Transitive broader concepts");
    private final CheckBox transitiveBroaderTopConcept = new CheckBox("Transitive broader top concepts");
    private final CheckBox relatedConcept = new CheckBox("Related concepts");
    private final TextArea query = new TextArea("Query");
    private final Logger logger = Logger.getLogger(PPXConfigDialog.class);

    public PPXConfigDialog(PPXConfig config) {
        this.config = config;
        apiPanel = new PoolPartyApiPanel(config.getApiConfig());
        addComponent(apiPanel);

        numberOfConcepts.setRequired(true);
        numberOfConcepts.setWidth("710px");
        numberOfConcepts.setValue(Integer.toString(config.getNumberOfConcepts()));
        numberOfConcepts.addValidator(new AbstractValidator("Must be a number.") {
            @Override
            public boolean isValid(Object value) {
                try {
                    Integer num = new Integer((String) value);
                    if (num > 0) {
                        return true;
                    }
                    return false;
                } catch (Exception ex) {
                    setErrorMessage(ex.getMessage());
                    return false;
                }
            }
        });

        addComponent(numberOfConcepts);

        numberOfTerms.setRequired(true);
        numberOfTerms.setWidth("710px");
        numberOfTerms.setValue(Integer.toString(config.getNumberOfTerms()));
        numberOfTerms.addValidator(new AbstractValidator("Must be a number.") {
            @Override
            public boolean isValid(Object value) {
                try {
                    Integer num = new Integer((String) value);
                    if (num >= 0) {
                        return true;
                    }
                    return false;
                } catch (Exception ex) {
                    setErrorMessage(ex.getMessage());
                    return false;
                }
            }
        });

        addComponent(numberOfTerms);

        language.addItem("en");
        language.addItem("de");
        language.setRequired(true);
        language.setValue(config.getLanguage());
        language.setNullSelectionAllowed(false);
        language.setDescription("The language of the extraction.");

        addComponent(language);

        query.setSizeFull();
        query.setValue(config.getQuery());
        query.setRows(20);
        query.setRequired(true);
        query.setDescription("?documentUri holds the identifier of the document (in this case the fragment) while ?text the text value of the document (for which annotations are created by PPX, in this case the value of the fragment).");

        addComponent(query);

        transitiveBroader.setValue(false);
        transitiveBroader.setDescription("Retrieve transitive broader concepts of the extracted concepts.");

        addComponent(transitiveBroader);

        transitiveBroaderTopConcept.setValue(false);
        transitiveBroaderTopConcept.setDescription("Retrieve transitive broader top concepts of the extracted concepts.");

        addComponent(transitiveBroaderTopConcept);

        relatedConcept.setValue(false);
        relatedConcept.setDescription("Retrieve related concepts of the extracted concepts.");

        addComponent(relatedConcept);
    }

    @Override
    public Object getConfig() {
        try {
            PoolPartyApiConfig apiConfig = apiPanel.getApiConfig();
            URL url = PPTApi.getServiceUrl(apiConfig.getServer(), "PoolParty/sparql/" + apiConfig.getUriSupplement() + "?query=" + URLEncoder.encode("ASK {?x a <http://www.w3.org/2004/02/skos/core#Concept> }", "UTF-8"));
            logger.info(url);
            Authentication authentication = apiConfig.getAuthentication();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            authentication.visit(con);
            if (con.getResponseCode() != 200) {
                getWindow().showNotification("Unable to query SPARQL endpoint of project", "", Window.Notification.TYPE_ERROR_MESSAGE);
                throw new RuntimeException("Response code: " + con.getResponseCode());
            }
            config.setApiConfig(apiConfig);
            config.setLanguage((String) language.getValue());
            String numberOfConceptsStr = (String) numberOfConcepts.getValue();
            int numberOfConceptsInt = Integer.parseInt(numberOfConceptsStr);
            config.setNumberOfConcepts(numberOfConceptsInt);
            String numberOfTermsStr = (String) numberOfTerms.getValue();
            int numberOfTermsInt = Integer.parseInt(numberOfTermsStr);
            config.setNumberOfTerms(numberOfTermsInt);
            config.setRelatedConcepts(relatedConcept.booleanValue());
            config.setTransitiveBroaderConcepts(transitiveBroader.booleanValue());
            config.setTransitiveBroaderTopConcepts(transitiveBroaderTopConcept.booleanValue());

        } catch (Exception ex) {
            logger.error("Unable to query SPARQL endpoint of project", ex);
        }
        return config;
    }
}
