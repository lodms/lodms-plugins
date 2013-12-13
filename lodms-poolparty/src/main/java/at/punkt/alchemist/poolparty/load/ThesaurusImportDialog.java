package at.punkt.alchemist.poolparty.load;

import at.punkt.alchemist.poolparty.PoolPartyApiPanel;
import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.*;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author kreisera
 */
public class ThesaurusImportDialog extends VerticalLayout implements ConfigDialog {

    private final Logger logger = Logger.getLogger(ThesaurusImportDialog.class);
    private final ThesaurusImportConfig config;
    private final ComboBox graph = new ComboBox("Graph");
    private PoolPartyApiPanel apiPanel;

    public ThesaurusImportDialog(ThesaurusImportConfig config) {
        this.config = config;
        apiPanel = new PoolPartyApiPanel(config.getApiConfig());
        addComponent(apiPanel);
        graph.addValidator(new AbstractStringValidator(null) {

            @Override
            protected boolean isValidString(String value) {
                try {
                    URIImpl u = new URIImpl(value);
                    return true;
                } catch (Exception ex){
                    setErrorMessage("Invalid Graph URI: "+ex.getMessage());
                    return false;
                }
            }
        });
        graph.setContainerDataSource(new BeanItemContainer(String.class, Arrays.asList("http://dbpedia.org", "http://dbpedia.org/categories", 
                "http://de.dbpedia.org", "http://de.dbpedia.org/categories", "http://freebase.org", "http://geonames.org", "http://umbel.org/",
                "http://sindice.com/", "http://wordnet.princeton.edu", "http://www.dmoz.org/")));
        if (config.getGraph() != null && !config.getGraph().isEmpty())
            graph.setValue(config.getGraph());
        addComponent(graph);
    }

    @Override
    public Object getConfig() {
        config.setApiConfig(apiPanel.getApiConfig());
        config.setGraph((String)graph.getValue());
        return config;
    }
}
