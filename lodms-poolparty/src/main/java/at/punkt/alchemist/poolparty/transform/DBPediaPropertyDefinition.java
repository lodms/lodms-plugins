package at.punkt.alchemist.poolparty.transform;

/**
 *
 * @author kreisera
 */
public class DBPediaPropertyDefinition {

    private String uri;
    private String label;
    private boolean literalValue;

    public boolean isLiteralValue() {
        return literalValue;
    }

    public void setLiteralValue(boolean literalValue) {
        this.literalValue = literalValue;
    }

    public DBPediaPropertyDefinition(String uri, String label) {
        this.uri = uri;
        this.label = label;
    }

    public DBPediaPropertyDefinition(String uri, String label, boolean literalValue) {
        this.uri = uri;
        this.label = label;
        this.literalValue = literalValue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
