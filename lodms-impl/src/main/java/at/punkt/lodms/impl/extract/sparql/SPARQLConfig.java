/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.extract.sparql;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class SPARQLConfig {
    
    private String endpoint = "";
    private String query = "";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
