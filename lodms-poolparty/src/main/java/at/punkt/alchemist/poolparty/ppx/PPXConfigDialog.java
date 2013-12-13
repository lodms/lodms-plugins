/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.alchemist.poolparty.ppx;

import at.punkt.lodms.impl.transform.ppx.PPXConfig;
import at.punkt.lodms.impl.transform.replace.regex.ValueType;
import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Kata
 */
public class PPXConfigDialog extends VerticalLayout implements ConfigDialog {

    private final PPXConfig config;
    private Form form;

    public PPXConfigDialog(PPXConfig oldConfig) {
        this.config = oldConfig;
        form = new Form();
        form.setSizeFull();
        form.setFormFieldFactory(new FormFieldFactory() {
            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                if (propertyId.equals("numberOfConcepts")) {
                    TextField f = new TextField("Number of concepts");
                    f.setRequired(true);
                    f.setWidth("710px");
                    f.addValidator(new AbstractValidator("Must be a number.") {
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
                    return f;
                } else if (propertyId.equals("projectId")) {
                    TextField f = new TextField("Thesaurus project id.");
                    f.setRequired(true);
                    f.setWidth("710px");
                    f.setRequired(true);
                    return f;
                } else if (propertyId.equals("language")) {
                    ComboBox field = new ComboBox("Language");
                    field.addItem("en");
                    field.addItem("de");
                    field.setRequired(true);
                    field.setNullSelectionAllowed(false);
                    field.setDescription("The language of the extraction.");
                    return field;
                } else if (propertyId.equals("server")) {
                    TextField f = new TextField("Server url (eg. http://localhost:8080/extractor).");
                    f.setRequired(true);
                    f.setWidth("710px");
                    f.setRequired(true);
                    return f;
                } else if (propertyId.equals("text")) {
                    TextArea field = new TextArea("Text");
                    field.setSizeFull();
                    field.setRows(20);
                    field.setRequired(true);
                    return field;
                }
                return null;
            }
        });
        form.setItemDataSource(new BeanItem<PPXConfig>(config));
        addComponent(form);
    }

    @Override
    public PPXConfig getConfig() {
        form.commit();
        return config;
    }
}
