package com.tenforce.lodms.transform.wkdtag;

import at.punkt.lodms.integration.ConfigDialog;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Form;
import com.vaadin.ui.VerticalLayout;

public class WkdSpotlightTaggerConfigDialog extends VerticalLayout implements ConfigDialog {
    private final WkdSpotlightTaggerConfig config;
    private Form form;

    public WkdSpotlightTaggerConfigDialog(WkdSpotlightTaggerConfig config) {
        this.config = config;
        form = new Form();
        form.setItemDataSource(new BeanItem<WkdSpotlightTaggerConfig>(config));
        addComponent(form);
    }

    /**
     * Returns the config object that has been configured using this dialog.<br/>
     * This method will be called when the user hits the "configure" button.
     *
     * @return
     */
    @Override
    public WkdSpotlightTaggerConfig getConfig() {
        form.commit();
        return config;
    }
}
