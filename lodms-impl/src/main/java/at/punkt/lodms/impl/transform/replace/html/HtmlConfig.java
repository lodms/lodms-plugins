/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.replace.html;

/**
 *
 * @author kreisera
 */
public class HtmlConfig {
    
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
