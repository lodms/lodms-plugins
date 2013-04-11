/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.extract.sparql;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.io.Serializable;
import java.net.URL;
import org.openrdf.query.parser.sparql.SPARQLParser;

/**
 *
 * @author Alex Kreiser
 */
public class SPARQLConfigDialog extends VerticalLayout implements ConfigDialog {

    private BeanItem<SPARQLConfig> beanItem;
    private Form form;

    public SPARQLConfigDialog(SPARQLConfig config) {
        form = new Form();
        addComponent(form);
        beanItem = new BeanItem<SPARQLConfig>(config);
        form.setImmediate(true);
        form.setFormFieldFactory(new FormFieldFactory() {

            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                if (propertyId.equals("endpoint")) {
                    TextField f = new TextField("Endpoint URL");
                    f.setRequired(true);
                    f.setWidth("710px");
                    f.addValidator(new AbstractValidator("Must be a valid URL") {

                        @Override
                        public boolean isValid(Object value) {
                            try {
                                URL url = new URL((String) value);
                                return true;
                            } catch (Exception ex) {
                                setErrorMessage(ex.getMessage());
                                return false;
                            }
                        }
                    });
                    return f;
                } else if (propertyId.equals("query")) {
                    TextArea field = new TextArea("SPARQL Query");
                    field.setSizeFull();
                    field.setRows(20);
                    field.setRequired(true);
                    field.addValidator(new AbstractStringValidator("Must be a valid SPARQL query") {

                        @Override
                        protected boolean isValidString(String value) {
                            SPARQLParser parser = new SPARQLParser();
                            try {
                                parser.parseQuery(value, null);
                                return true;
                            } catch (Exception ex) {
                                return false;
                            }
                        }
                    });
                    return field;
                }
                return null;
            }
        });
        form.setItemDataSource(beanItem);
        form.focus();
    }

    @Override
    public Serializable getConfig() {
        form.commit();
        return beanItem.getBean();
    }
}
