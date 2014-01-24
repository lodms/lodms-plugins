package com.tenforce.lodms.transform.wkdtag;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.openrdf.model.URI;

import java.util.ArrayList;
import java.util.List;

public class SpotlightAnnotation {
    @JsonProperty("Resources")
    private List<SpotlightResource> resources = new ArrayList<SpotlightResource>();
    private URI fragmentURI;

    public List<SpotlightResource> getResources() {
        return resources;
    }

    public void setResources(List<SpotlightResource> resources) {
        this.resources = resources;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do nothing with unknown attributes
    }

    public URI getFragmentURI() {
        return fragmentURI;
    }

    public void setFragmentURI(URI fragmentURI) {
        this.fragmentURI = fragmentURI;
    }
}
