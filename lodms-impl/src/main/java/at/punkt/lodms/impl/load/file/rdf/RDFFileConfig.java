/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.load.file.rdf;

import java.io.Serializable;

/**
 *
 * @author Alex Kreiser
 */
public class RDFFileConfig implements Serializable {

    private String filePath = "";
    private String format = "";
    private String graph = "";

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }
}
