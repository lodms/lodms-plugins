package at.punkt.alchemist.poolparty.load;

import at.punkt.alchemist.poolparty.PoolPartyApiConfig;

/**
 *
 * @author kreisera
 */
public class ThesaurusImportConfig {

    private PoolPartyApiConfig apiConfig;
    private String graph;

    public PoolPartyApiConfig getApiConfig() {
        return apiConfig;
    }

    public void setApiConfig(PoolPartyApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }
}
