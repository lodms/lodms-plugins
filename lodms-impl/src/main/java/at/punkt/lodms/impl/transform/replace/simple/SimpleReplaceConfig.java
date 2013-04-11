/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.replace.simple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author kreisera
 */
public class SimpleReplaceConfig implements Serializable {

    private List<URI> triggerProperties = new ArrayList<URI>();
    private Class<? extends Value> objectType = Value.class;
    private Class<? extends Value> replacementType;
    private Properties replacements = new Properties();

    public Class<? extends Value> getObjectType() {
        return objectType;
    }

    public void setObjectType(Class<? extends Value> objectType) {
        this.objectType = objectType;
    }

    public Class<? extends Value> getReplacementType() {
        return replacementType;
    }

    public void setReplacementType(Class<? extends Value> replacementType) {
        this.replacementType = replacementType;
    }

    public Properties getReplacements() {
        return replacements;
    }

    public void setReplacements(Properties replacements) {
        this.replacements = replacements;
    }

    public List<URI> getTriggerProperties() {
        return triggerProperties;
    }

    public void setTriggerProperties(List<URI> triggerProperties) {
        this.triggerProperties = triggerProperties;
    }
}
