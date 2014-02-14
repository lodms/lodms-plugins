/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.alchemist.poolparty.ppx;

import at.punkt.alchemist.poolparty.PoolPartyApiConfig;

/**
 *
 * @author Kata
 */
public class PPXConfig {

    private PoolPartyApiConfig apiConfig;
    private int numberOfConcepts = 10;
    private int numberOfTerms = 0;
    private boolean transitiveBroaderConcepts = false;
    private boolean transitiveBroaderTopConcepts = false;
    private boolean relatedConcepts = false;
    private String query = "prefix bibo:<http://purl.org/ontology/bibo/>\n"
            + "     prefix metalex:<http://www.metalex.eu/metalex/2008-05-02#>\n"
            + "            \n"
            + "            SELECT   ?documentUri ?text\n"
            + "            WHERE  {\n"
            + "             GRAPH ?graph { \n"
            + "               { {?law a bibo:Legislation} UNION {?law a bibo:LegalDecision} } .\n"
            + "               ?law metalex:fragment  ?documentUri .\n"
            + "               ?documentUri  rdf:value ?text .\n"
            + "                }    \n"
            + "            }";
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getNumberOfConcepts() {
        return numberOfConcepts;
    }

    public void setNumberOfConcepts(int numberOfConcepts) {
        this.numberOfConcepts = numberOfConcepts;
    }

    public int getNumberOfTerms() {
        return numberOfTerms;
    }

    public void setNumberOfTerms(int numberOfTerms) {
        this.numberOfTerms = numberOfTerms;
    }

    public boolean isRelatedConcepts() {
        return relatedConcepts;
    }

    public void setRelatedConcepts(boolean relatedConcepts) {
        this.relatedConcepts = relatedConcepts;
    }

    public boolean isTransitiveBroaderConcepts() {
        return transitiveBroaderConcepts;
    }

    public void setTransitiveBroaderConcepts(boolean transitiveBroaderConcepts) {
        this.transitiveBroaderConcepts = transitiveBroaderConcepts;
    }

    public boolean isTransitiveBroaderTopConcepts() {
        return transitiveBroaderTopConcepts;
    }

    public void setTransitiveBroaderTopConcepts(boolean transitiveBroaderTopConcepts) {
        this.transitiveBroaderTopConcepts = transitiveBroaderTopConcepts;
    }

    public PoolPartyApiConfig getApiConfig() {
        return apiConfig;
    }

    public void setApiConfig(PoolPartyApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }
}
