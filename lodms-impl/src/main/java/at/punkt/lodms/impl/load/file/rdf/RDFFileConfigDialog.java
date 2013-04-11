/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.load.file.rdf;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.io.Serializable;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Alex Kreiser
 */
public class RDFFileConfigDialog extends VerticalLayout implements ConfigDialog {

    private final RDFFileConfig config;
    Form form= new Form() ;

    public RDFFileConfigDialog(RDFFileConfig config) {
        this.config = config;
        form.setFormFieldFactory(new DefaultFieldFactory() {

            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                if (propertyId.equals("filePath")) {
                    TextField field = new TextField("Absolute file path");
                    field.setRequired(true);
                    field.setRequiredError("File path is required!");
                    field.setWidth(690, UNITS_PIXELS);
                    return field;
                } else if (propertyId.equals("format")) {
                    ComboBox comboBox = new ComboBox("RDF Format");
                    comboBox.setRequired(true);
                    comboBox.setRequiredError("RDF Format is required");
                    comboBox.setNullSelectionAllowed(false);
                    for (RDFFormat format : RDFFormat.values()) {
                        comboBox.addItem(format.getName());
                    }
                    return comboBox;
                } else if (propertyId.equals("graph")) {
                    TextField field = new TextField("Graph");
                    field.setRequired(false);
                    field.setWidth(690, UNITS_PIXELS);
                    field.addValidator(new AbstractStringValidator(null) {

                        @Override
                        protected boolean isValidString(String value) {
                            try {
                                URI u = new URIImpl(value);
                                return true;
                            } catch (Exception ex) {
                                setErrorMessage("Invalid Graph URI: "+ex.getMessage());
                                return false;
                            }
                        }
                    });
                    return field;
                }
                return super.createField(item, propertyId, uiContext);
            }
        });
        form.setItemDataSource(new BeanItem<RDFFileConfig>(this.config));
        addComponent(form);
    }

    @Override
    public Serializable getConfig() {
        form.commit();
        return config;
    }

}