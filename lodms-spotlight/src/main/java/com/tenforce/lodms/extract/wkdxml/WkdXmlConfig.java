package com.tenforce.lodms.extract.wkdxml;

public class WkdXmlConfig {
    private String xsltPath="";
    private String XMlPath="";
    private String namespace="http://schema.mycompany.com/resource/";

    public String getXsltPath() {
        return xsltPath;
    }

    public void setXsltPath(String xsltPath) {
        this.xsltPath = xsltPath;
    }

    public String getXMlPath() {
        return XMlPath;
    }



    public void setXMlPath(String XMlPath) {
        this.XMlPath = XMlPath;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}