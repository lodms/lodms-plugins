package com.tenforce.lodms.extract.wkdxml;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.VerticalLayout;

public class WkdXmlConfigDialog extends VerticalLayout implements ConfigDialog {
    private BeanItem<WkdXmlConfig> beanItem;
    private Form form;

    public WkdXmlConfigDialog(WkdXmlConfig config) {
        form = new Form();
        addComponent(form);
        beanItem = new BeanItem<WkdXmlConfig>(config);
        form.setImmediate(true);
        form.setFormFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field createField(Item item, Object propertyId, Component uiContext) {
                Field f = super.createField(item, propertyId, uiContext);
                f.setRequired(true);
                f.setWidth(500,UNITS_PIXELS);
                return f;
            }
        });
        form.setItemDataSource(beanItem);
        form.focus();
    }

    @Override
    public Object getConfig() {
        form.commit();
        return beanItem.getBean();
    }
}
