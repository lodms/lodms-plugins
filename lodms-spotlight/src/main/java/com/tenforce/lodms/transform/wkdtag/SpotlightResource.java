package com.tenforce.lodms.transform.wkdtag;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpotlightResource {
    @JsonProperty("@URI")
    private String URI;
    @JsonProperty("@similarityScore")
    private float similarityScore ;
    @JsonProperty("@surfaceForm")
    private String surfaceForm;
    @JsonProperty("@offset")
    private int offset;

    @JsonAnySetter
    public void set(String name, Object value) {
        //ignore other values
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public float getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(float similarityScore) {
        this.similarityScore = similarityScore;
    }

    public String getSurfaceForm() {
        return surfaceForm;
    }

    public void setSurfaceForm(String surfaceForm) {
        this.surfaceForm = surfaceForm;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
