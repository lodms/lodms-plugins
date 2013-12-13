package at.punkt.alchemist.poolparty.transform;

import java.util.Collection;

/**
 *
 * @author kreisera
 */
public class DBPediaProperty {

    private String uri;
    private String languageFilter;

    public DBPediaProperty() {
    }

    public DBPediaProperty(String uri) {
        this.uri = uri;
    }
    
    public String getLanguageFilter() {
        return languageFilter;
    }

    public void setLanguageFilter(String languageFilter) {
        this.languageFilter = languageFilter;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public static String toQuery(Collection<DBPediaProperty> properties, String concept, String link) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CONSTRUCT { ");
        int num = 0;
        for (DBPediaProperty p : properties) {
            queryBuilder.append("<").append(concept).append("> <").append(p.getUri()).append("> ?v").append(num).append(". ");
            num++;
        }
        queryBuilder.append("} WHERE { ");
        num = 0;
        for (DBPediaProperty p : properties) {
            if (num > 0)
                queryBuilder.append("UNION ");
            queryBuilder.append("{ <").append(link).append("> <").append(p.getUri()).append("> ?v").append(num).append(". ");
            if (p.getLanguageFilter() != null && !p.getLanguageFilter().isEmpty()) {
                queryBuilder.append("FILTER (lang(?v").append(num).append(") = \"").append(p.getLanguageFilter()).append("\") ");
            }
            queryBuilder.append("} ");
            num++;
        }
        queryBuilder.append("}");
        return queryBuilder.toString();
    }
}
