/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.delete;

import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.URI;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class ResourceDeleteConfig {
    
    private List<URI> resources = new ArrayList<URI>();

    public List<URI> getResources() {
        return resources;
    }

    public void setResources(List<URI> resources) {
        this.resources = resources;
    }
}
