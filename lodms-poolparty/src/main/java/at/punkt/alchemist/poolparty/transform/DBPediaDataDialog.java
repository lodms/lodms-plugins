package at.punkt.alchemist.poolparty.transform;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author kreisera
 */
public class DBPediaDataDialog extends VerticalLayout implements ConfigDialog {

    private final Logger logger = Logger.getLogger(DBPediaDataDialog.class);
    private final DBPediaDataConfig config;
    private List<DBPediaPropertyDefinition> properties = new ArrayList<DBPediaPropertyDefinition>();
    private final OptionGroup propertyGroup = new OptionGroup("Properties");
    private final ComboBox graph = new ComboBox("DBpedia Language");

    public DBPediaDataDialog(DBPediaDataConfig config) {
        this.config = config;
        
        properties.add(new DBPediaPropertyDefinition("http://www.w3.org/2000/01/rdf-schema#label", "Label", true));
        properties.add(new DBPediaPropertyDefinition("http://dbpedia.org/ontology/abstract", "Abstract", true));
        properties.add(new DBPediaPropertyDefinition("http://dbpedia.org/ontology/thumbnail", "Thumbnail", false));
        properties.add(new DBPediaPropertyDefinition("http://purl.org/dc/terms/subject", "Subject", true));
        properties.add(new DBPediaPropertyDefinition("http://www.w3.org/2003/01/geo/wgs84_pos#lat", "Latitude", false));
        properties.add(new DBPediaPropertyDefinition("http://www.w3.org/2003/01/geo/wgs84_pos#long", "Longitude", false));
        properties.add(new DBPediaPropertyDefinition("http://xmlns.com/foaf/0.1/depiction", "Depiction", false));
        properties.add(new DBPediaPropertyDefinition("http://xmlns.com/foaf/0.1/isPrimaryTopicOf", "Primary topic of", false));
        properties.add(new DBPediaPropertyDefinition("http://xmlns.com/foaf/0.1/homepage", "Homepage", false));
        
        propertyGroup.setMultiSelect(true);
        propertyGroup.setItemCaptionMode(OptionGroup.ITEM_CAPTION_MODE_PROPERTY);
        propertyGroup.setItemCaptionPropertyId("label");
        propertyGroup.setContainerDataSource(new BeanItemContainer(DBPediaPropertyDefinition.class, properties));
        for (DBPediaProperty prop : config.getProperties()) {
            for (DBPediaPropertyDefinition def : properties) {
                if (prop.getUri().equals(def.getUri())) {
                    propertyGroup.select(def);
                }}
        }
        addComponent(propertyGroup);
        propertyGroup.setRequired(true);
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
        graph.setNullSelectionAllowed(false);
        graph.setRequired(true);
        graph.setContainerDataSource(new BeanItemContainer(String.class, Arrays.asList("http://en.dbpedia.org", "http://de.dbpedia.org", "http://fr.dbpedia.org", "http://es.dbpedia.org")));
        if (config.getGraph() != null && !config.getGraph().isEmpty())
            graph.setValue(config.getGraph());
        addComponent(graph);
    }

    @Override
    public Object getConfig() {
        propertyGroup.commit();
        ArrayList<DBPediaProperty> props = new ArrayList<DBPediaProperty>();
        for (DBPediaPropertyDefinition def : ((Collection<DBPediaPropertyDefinition>)propertyGroup.getValue())) {
            props.add(new DBPediaProperty(def.getUri()));
        }
        config.setProperties(props);
        return config;
    }
}