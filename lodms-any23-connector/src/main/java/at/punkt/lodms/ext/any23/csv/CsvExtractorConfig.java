/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.ext.any23.csv;

import java.io.Serializable;

/**
 *
 * @author Alex Kreiser
 */
public class CsvExtractorConfig implements Serializable {
    
    private String filePath = "";
    private String baseUri = "http://example.com/csv/";

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
}
