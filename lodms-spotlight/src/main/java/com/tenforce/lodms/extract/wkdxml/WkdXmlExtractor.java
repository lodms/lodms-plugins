package com.tenforce.lodms.extract.wkdxml;

import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.ExtractException;
import at.punkt.lodms.spi.extract.Extractor;
import com.vaadin.Application;
import com.vaadin.terminal.Resource;
import org.apache.log4j.Logger;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

//import com.sun.org.apache.xml.internal.resolver.CatalogManager;
//import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;


public class WkdXmlExtractor extends ConfigurableBase<WkdXmlConfig> implements Extractor, UIComponent,ConfigDialogProvider<WkdXmlConfig> {
    private List<String> warnings;
    private Logger log = Logger.getLogger(WkdXmlExtractor.class);
    private Transformer transformer;
    private CatalogManager catalogManager = new CatalogManager("CatalogManager.properties");
    private CatalogResolver uriResolver  = new CatalogResolver(catalogManager);
    @Override
    protected void configureInternal(WkdXmlConfig config) throws ConfigurationException {
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try {
            transformerFactory.setURIResolver(uriResolver);
            InputStream stream = getStreamFromPath(config.getXsltPath());
            StreamSource streamSource = new StreamSource(stream,config.getXsltPath());
            transformer = transformerFactory.newTransformer(streamSource);
            log.info("loaded xslt:" + config.getXsltPath());
        } catch (TransformerConfigurationException e) {
            throw new ConfigurationException(e.getMessage(),e);
        } catch (IOException e) {
            throw new ConfigurationException(e.getMessage(),e);
        }
    }

    private InputStream getStreamFromPath(String path) throws IOException {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            URL url = new URL(path);
            return url.openStream();
        }
        else {
            return new FileInputStream(path);
        }
    }

    /**
     * Extracts data from a data source and converts it to RDF.<br/>
     *
     * @param handler This handler has to be used to store the produced RDF statements.<br/>
     * @param context Context for one extraction cycle containing meta information about the extraction.
     * @throws at.punkt.lodms.spi.extract.ExtractException
     *          If any error occurs troughout the extraction cycle.
     */
    @Override
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
        warnings = context.getWarnings();
        try {
            transformFile(handler,config.getXMlPath());
        }
        catch (Exception e) {
            throw new ExtractException(e.getMessage(),e);
        }
    }

    public void transformFile(RDFHandler handler,String xmlPath) throws ExtractException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            StreamResult transformResult = new StreamResult(outputStream);
            InputStream stream = getStreamFromPath(xmlPath);
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setEntityResolver(uriResolver);
            ValiantXMLErrorHandler valiantHandler = new ValiantXMLErrorHandler();
            xmlReader.setErrorHandler(valiantHandler);
            SAXSource inputSource = new SAXSource(xmlReader, new InputSource(stream));

            transformer.transform(inputSource, transformResult);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
            rdfParser.setRDFHandler(handler);
            rdfParser.parse(inputStream, config.getNamespace());
            inputStream.close();
            stream.close();
        } catch (TransformerException e) {
            log.warn("Failed to transform file <" + xmlPath + ">: " + e.getMessage());
            warnings.add("Failed to transform file <" + xmlPath + ">: " + e.getMessage());
            throw new ExtractException(e.getMessage(),e);
        } catch (RDFHandlerException e) {
            throw new ExtractException(e.getMessage(),e);
        } catch (RDFParseException e) {
            log.warn("Failed to parse rdf file: " + e.getMessage());
            warnings.add("Failed to parse rdf file: " + e.getMessage());
            throw new ExtractException(e.getMessage(),e);
        } catch (IOException e) {
            throw new ExtractException(e.getMessage(),e);
        } catch (SAXException e) {
            throw new ExtractException(e.getMessage(),e);
        }
    }

    /**
     * Returns a short, self-descriptive name of the component.
     *
     * @return
     */
    @Override
    public String getName() {
        return "WKD XML Extractor";
    }

    /**
     * Returns a description of what functionality this component provides.
     *
     * @return
     */
    @Override
    public String getDescription() {
        return "Converts XML files to RDF using a configurable xslt";
    }

    /**
     * Returns an icon as vaadin {@link com.vaadin.terminal.Resource}, {@code null} if no icon is available.
     *
     * @param application
     * @return
     */
    @Override
    public Resource getIcon(Application application) {
        return null;
    }

    /**
     * Returns a string representing the configured internal state of this component.<br/>
     * This will be used to display this component after having been configured.
     *
     * @return
     */
    @Override
    public String asString() {
        return this.getName() + ": " + this.getConfig().getXsltPath();
    }

    /**
     * Returns a new {@link at.punkt.lodms.integration.ConfigDialog} instance that will be embedded in the
     * dialog window on configuration of this component.
     *
     * @param config An already existing configuration object<br/>
     *               {@code null} if this is the first configuration of the component
     * @return
     */
    @Override
    public ConfigDialog getConfigDialog(WkdXmlConfig config) {
        return new WkdXmlConfigDialog(config);
    }

    /**
     * Returns a new (blank) JavaBean instance with its default values set.
     *
     * @return
     */
    @Override
    public WkdXmlConfig newDefaultConfig() {
        return new WkdXmlConfig();
    }
}
