package com.tenforce.lodms.transform.wkdtag;

public class WkdSpotlightTaggerConfig {
    private String spotlightUrl = "http://de.dbpedia.org/spotlight/rest/annotate";
    private double confidence = 0.2;
    private int support = 20;
    public String getSpotlightUrl() {
        return spotlightUrl;
    }

    public void setSpotlightUrl(String spotlightUrl) {
        this.spotlightUrl = spotlightUrl;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }
}
