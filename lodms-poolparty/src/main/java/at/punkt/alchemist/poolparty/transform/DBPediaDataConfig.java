package at.punkt.alchemist.poolparty.transform;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kreisera
 */
public class DBPediaDataConfig {

    private List<DBPediaProperty> properties = new ArrayList<DBPediaProperty>();
    private String graph;

    public List<DBPediaProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<DBPediaProperty> properties) {
        this.properties = properties;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }
}
