/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.ext.any23.html;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Alex Kreiser
 */
public class Any23Dialog extends VerticalLayout implements ConfigDialog {

    private final BeanItemContainer<Any23Document> container;
    private Any23Config config = new Any23Config();
    private final Logger logger = Logger.getLogger(Any23Dialog.class);

    public Any23Dialog(Any23Config config) {
        container = new BeanItemContainer<Any23Document>(Any23Document.class, config.getDocuments());
        Table table = new Table("Extract URLs");
        table.setContainerDataSource(container);
        table.setColumnWidth("url", 780);
        addComponent(table);
        
        addComponent(new Label("URL"));
        HorizontalLayout addSourceLayout = new HorizontalLayout();
        final TextField sourceUrl = new TextField();
        sourceUrl.addValidator(new AbstractStringValidator("Must be a valid URL") {

            @Override
            protected boolean isValidString(String value) {
                try {
                    URL url = new URL(value);
                    return true;
                } catch (MalformedURLException ex) {
                    return false;
                }
            }
        });
        sourceUrl.setWidth("700px");
        addSourceLayout.addComponent(sourceUrl);
        Button addSourceButton = new Button("Add");
        
        addSourceButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                sourceUrl.commit();
                Any23Document value = new Any23Document();
                value.setUrl((String)sourceUrl.getValue());
                try {
                    container.addBean(value);
                    sourceUrl.setValue("");
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        });
        addSourceLayout.addComponent(addSourceButton);
        addComponent(addSourceLayout);
    }

    @Override
    public Serializable getConfig() {
        config.setDocuments(new ArrayList(container.getItemIds()));
        return config;
    }
}