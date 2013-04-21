/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.sparql.update;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import org.openrdf.query.parser.ParsedUpdate;
import org.openrdf.query.parser.sparql.SPARQLParser;

/**
 *
 * @author kreisera
 */
public class SPARQLUpdateTransformDialog extends VerticalLayout implements ConfigDialog {

    private final SPARQLUpdateTransformConfig config;
    private Form queryForm;

    public SPARQLUpdateTransformDialog(SPARQLUpdateTransformConfig oldConfig) {
        this.config = oldConfig;
        queryForm = new Form();
        queryForm.setSizeFull();
        queryForm.setFormFieldFactory(new FormFieldFactory() {

            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                if (propertyId.equals("query")) {
                    TextArea query = new TextArea("SPARQL Query");
                    query.setSizeFull();
                    query.setRows(25);
                    query.addValidator(new AbstractStringValidator("Must be a valid UPDATE query!") {

                        @Override
                        protected boolean isValidString(String value) {
                            SPARQLParser parser = new SPARQLParser();
                            try {
                                ParsedUpdate parsed = parser.parseUpdate(value, null);
                            } catch (Exception ex) {
                                setErrorMessage(ex.getMessage());
                                return false;
                            }
                            return true;
                        }
                    });
                    return query;
                }
                return null;
            }
        });
        queryForm.setItemDataSource(new BeanItem<SPARQLUpdateTransformConfig>(config));
        addComponent(queryForm);
    }

    @Override
    public SPARQLUpdateTransformConfig getConfig() {
        queryForm.commit();
        return config;
    }
}