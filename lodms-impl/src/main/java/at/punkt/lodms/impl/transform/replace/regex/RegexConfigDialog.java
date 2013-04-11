/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.replace.regex;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.VerticalLayout;
import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author kreisera
 */
public class RegexConfigDialog extends VerticalLayout implements ConfigDialog {

    private final RegexConfig config;
    private Form configForm;

    private class RegexValidator extends AbstractStringValidator {

        public RegexValidator(String errorMessage) {
            super(errorMessage);
        }

        @Override
        protected boolean isValidString(String value) {
            try {
                Pattern.compile(value);
                return true;
            } catch (PatternSyntaxException ex) {
                this.setErrorMessage(ex.getMessage());
                return false;
            }
        }
    }

    public RegexConfigDialog(RegexConfig config) {
        this.config = config;
        configForm = new Form();
        configForm.setFormFieldFactory(new DefaultFieldFactory() {

            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                if (propertyId.equals("valueType")) {
                    ComboBox field = new ComboBox("Object RDF Type");
                    field.addItem(ValueType.LITERAL);
                    field.addItem(ValueType.URI);
                    field.setRequired(true);
                    field.setNullSelectionAllowed(false);
                    field.setDescription("The type of RDF value that should be replaced via regular expression");
                    return field;
                } else if (propertyId.equals("regex")) {
                    Field field = super.createField(item, propertyId, uiContext);
                    field.setWidth(690, UNITS_PIXELS);
                    field.setRequired(true);
                    field.setRequiredError("Please provide a regular expression.");
                    field.setDescription("The regular expression that will be searched for.");
                    field.addValidator(new RegexValidator("Please provide a valid regular expression"));
                    return field;
                } else if (propertyId.equals("replacement")) {
                    Field field = super.createField(item, propertyId, uiContext);
                    field.setWidth(690, UNITS_PIXELS);
                    field.setRequired(false);
                    field.setRequiredError("Please provide a replacement.");
                    field.addValidator(new RegexValidator("Please provide a valid regular expression"));
                    field.setDescription("The regular expression that will be used for replacement.");
                    return field;
                }
                return super.createField(item, propertyId, uiContext);
            }
        });
        BeanItem<RegexConfig> beanItem = new BeanItem<RegexConfig>(config);
        configForm.setItemDataSource(beanItem);
        addComponent(configForm);
    }

    @Override
    public Serializable getConfig() {
        configForm.commit();
        return config;
    }
}
