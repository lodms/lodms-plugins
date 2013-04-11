/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.replace.html;

import java.io.Serializable;

/**
 *
 * @author kreisera
 */
public class HtmlConfig implements Serializable {
    
    private boolean convertHtmlEntities = true;
    private boolean stripHtmlTags = true;

    public boolean isConvertHtmlEntities() {
        return convertHtmlEntities;
    }

    public void setConvertHtmlEntities(boolean convertHtmlEntities) {
        this.convertHtmlEntities = convertHtmlEntities;
    }

    public boolean isStripHtmlTags() {
        return stripHtmlTags;
    }

    public void setStripHtmlTags(boolean stripHtmlTags) {
        this.stripHtmlTags = stripHtmlTags;
    }
    
}
