/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.delete;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ResourceDeleteDialog extends VerticalLayout implements ConfigDialog {

    private final ResourceDeleteConfig config;
    private BeanItemContainer<URI> resourcesToDelete;
    private final ValueFactory factory = ValueFactoryImpl.getInstance();

    public ResourceDeleteDialog(ResourceDeleteConfig config) {
        this.config = config;
        Table resourcesTable = new Table("Trigger RDF Properties");
        resourcesToDelete = new BeanItemContainer<URI>(URI.class);
        resourcesToDelete.addAll(config.getResources());
        resourcesTable.setWidth(100, UNITS_PERCENTAGE);
        resourcesTable.setHeight(300, UNITS_PIXELS);
        resourcesTable.setContainerDataSource(resourcesToDelete);
        resourcesTable.setVisibleColumns(new String[] { });
        resourcesTable.addGeneratedColumn("property", new Table.ColumnGenerator() {

            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                return new Label(itemId.toString());
            }
        });
        resourcesTable.addGeneratedColumn("delete", new Table.ColumnGenerator() {

            @Override
            public Component generateCell(Table source, final Object itemId, Object columnId) {
                Button delete = new Button("X");
                delete.addListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        resourcesToDelete.removeItem(itemId);
                    }
                });
                return delete;
            }
        });
        resourcesTable.setColumnWidth("delete", 50);
        resourcesTable.setSelectable(true);
        addComponent(resourcesTable);
        
        Label label = new Label("Resource to Delete");
        addComponent(label);
        
        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.setWidth(100, UNITS_PERCENTAGE);
        final TextField deleteResource = new TextField();
        deleteResource.setWidth(700, UNITS_PIXELS);
        addLayout.addComponent(deleteResource);
        Button addButton = new Button("Add", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                URI resource = factory.createURI((String)deleteResource.getValue());
                resourcesToDelete.addBean(resource);
                deleteResource.setValue("");
            }
        });
        addLayout.addComponent(addButton);
        addComponent(addLayout);
    }
    
    @Override
    public ResourceDeleteConfig getConfig() {
        config.getResources().clear();
        config.getResources().addAll(resourcesToDelete.getItemIds());
        return config;
    }
}