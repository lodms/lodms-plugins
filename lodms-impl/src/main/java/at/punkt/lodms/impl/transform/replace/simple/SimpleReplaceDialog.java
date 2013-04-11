/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.replace.simple;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 *
 * @author kreisera
 */
public class SimpleReplaceDialog extends GridLayout implements ConfigDialog {

    private final SimpleReplaceConfig config;
    private ComboBox objectTypes;
    private ComboBox replaceTypes;
    private BeanItemContainer<URI> triggerProperties;
    private final ValueFactory factory = ValueFactoryImpl.getInstance();
    private Properties replacements = new Properties();
    private final Logger logger = Logger.getLogger(SimpleReplaceDialog.class);
    private final TextArea replacementText = new TextArea("");

    private class PropertiesReceiver implements Upload.Receiver {

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public SimpleReplaceDialog(SimpleReplaceConfig conf) {
        super(2, 5);
        this.config = conf;
        objectTypes = new ComboBox("Target RDF Type");
        objectTypes.setDescription("Limits the statements that should be replaced to objects of this type");
        BeanItemContainer<Class> types = new BeanItemContainer<Class>(Class.class);
        objectTypes.setContainerDataSource(types);
        objectTypes.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        objectTypes.setItemCaptionPropertyId("simpleName");
        objectTypes.setNullSelectionAllowed(false);
        objectTypes.addItem(Literal.class);
        objectTypes.addItem(URI.class);
        objectTypes.addItem(BNode.class);
        objectTypes.select(conf.getObjectType());

        addComponent(objectTypes, 0, 0);

        replaceTypes = new ComboBox("Replacement RDF Type");
        replaceTypes.setDescription("The replacement values are casted to this RDF type");
        replaceTypes.setContainerDataSource(new BeanItemContainer<Class>(Class.class));
        replaceTypes.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        replaceTypes.setItemCaptionPropertyId("simpleName");
        replaceTypes.setNullSelectionAllowed(false);
        replaceTypes.addItem(Literal.class);
        replaceTypes.addItem(URI.class);
        replaceTypes.select(conf.getReplacementType());
        addComponent(replaceTypes, 0, 1);

        Table triggerTable = new Table("Trigger RDF Properties");
        triggerProperties = new BeanItemContainer<URI>(URI.class);
        triggerProperties.addAll(conf.getTriggerProperties());
        triggerTable.setWidth(100, UNITS_PERCENTAGE);
        triggerTable.setHeight(300, UNITS_PIXELS);
        triggerTable.setContainerDataSource(triggerProperties);
        triggerTable.setVisibleColumns(new String[] { });
        triggerTable.addGeneratedColumn("property", new Table.ColumnGenerator() {

            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                return new Label(itemId.toString());
            }
        });
        triggerTable.addGeneratedColumn("delete", new Table.ColumnGenerator() {

            @Override
            public Component generateCell(Table source, final Object itemId, Object columnId) {
                Button delete = new Button("X");
                delete.addListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        triggerProperties.removeItem(itemId);
                    }
                });
                return delete;
            }
        });
        triggerTable.setColumnWidth("delete", 50);
        triggerTable.setSelectable(true);
        addComponent(triggerTable, 0, 2);

        HorizontalLayout addPropertyLayout = new HorizontalLayout();
        addPropertyLayout.setWidth(100, UNITS_PERCENTAGE);
        Label triggerLabel = new Label("Trigger Property URI:");
        addComponent(triggerLabel, 0, 3);
        final TextField triggerProperty = new TextField();
        triggerProperty.setWidth(300, UNITS_PIXELS);
        addPropertyLayout.addComponent(triggerProperty);
        Button addProperty = new Button("Add");
        addProperty.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                triggerProperty.commit();
                String property = (String) triggerProperty.getValue();
                triggerProperties.addBean(factory.createURI(property));
                triggerProperty.setValue("");
            }
        });
        addPropertyLayout.addComponent(addProperty);
        addComponent(addPropertyLayout, 0, 4);

        replacementText.setSizeFull();
        replacementText.addValidator(new AbstractStringValidator("Must be in the properties format!") {

            @Override
            protected boolean isValidString(String value) {
                try {
                    replacements.load(new StringReader(value));
                    return true;
                } catch (IOException ex) {
                    return false;
                }
            }
        });
        StringWriter writer = new StringWriter();
        try {
            conf.getReplacements().store(writer, "Colons in URIs have to be replaced with backslash like this: \\:");
        } catch (IOException ex) {
            logger.error("Unable to serialize properties into text area", ex);
        }
        replacementText.setValue(writer.toString());
        replacementText.setHeight(400, UNITS_PIXELS);
        addComponent(replacementText, 1, 2, 1, 4);
        setSizeFull();
        setColumnExpandRatio(0, 0.35f);
        setColumnExpandRatio(1, 0.65f);
    }

    @Override
    public Serializable getConfig() {
        config.setObjectType((Class)objectTypes.getValue());
        config.setReplacementType((Class)replaceTypes.getValue());
        config.getTriggerProperties().clear();
        config.getTriggerProperties().addAll(triggerProperties.getItemIds());
        replacementText.commit();
        try {
            replacements.load(new StringReader((String)replacementText.getValue()));
        } catch (IOException ex) {
            logger.error(ex);
        }
        config.setReplacements(replacements);
        return config;
    }
}
