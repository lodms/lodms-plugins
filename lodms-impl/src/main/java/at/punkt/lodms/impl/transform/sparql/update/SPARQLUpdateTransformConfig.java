/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.sparql.update;

import java.io.Serializable;

/**
 *
 * @author kreisera
 */
public class SPARQLUpdateTransformConfig implements Serializable {

    private String query = "";

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
