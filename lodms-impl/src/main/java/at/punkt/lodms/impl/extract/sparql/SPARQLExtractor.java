/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.extract.sparql;

import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.ExtractException;
import at.punkt.lodms.spi.extract.Extractor;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class SPARQLExtractor extends ConfigurableBase<SPARQLConfig> implements Extractor, UIComponent, ConfigDialogProvider<SPARQLConfig> {

    protected URL endpoint;
    protected String query;
    protected RDFFormat format = RDFFormat.RDFXML;
    protected String encoding = "UTF-8";
    protected String baseUri = "";

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public URL getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(URL endpoint) {
        this.endpoint = endpoint;
    }

    public RDFFormat getFormat() {
        return format;
    }

    public void setFormat(RDFFormat format) {
        this.format = format;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
        try {
            URL call = new URL(endpoint.toString() + "?query=" + URLEncoder.encode(query, encoding) + "&format=" + URLEncoder.encode(format.getDefaultMIMEType(), encoding));
            HttpURLConnection connection = (HttpURLConnection) call.openConnection();
            connection.addRequestProperty("Accept", format.getDefaultMIMEType());
            RDFParser parser = Rio.createParser(format);
            parser.setRDFHandler(handler);
            parser.parse(connection.getInputStream(), baseUri);
        } catch (Exception ex) {
            throw new ExtractException(ex);
        }
    }

    @Override
    public String getName() {
        return "SPARQL Extractor";
    }

    @Override
    public String getDescription() {
        return "Extracts RDF from SPARQL Endpoints using graph queries (CONSTRUCT, DESCRIBE)";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/impl/component.png", application);
    }

    @Override
    public String asString() {
        String shortQuery = query;
        if (shortQuery.length() > 25) {
            shortQuery = shortQuery.substring(0, 25);
        }
        return getName() + " [" + endpoint + "] [" + shortQuery + "...]";
    }

    @Override
    public ConfigDialog getConfigDialog(SPARQLConfig config) {
        return new SPARQLConfigDialog(config);
    }

    @Override
    protected void configureInternal(SPARQLConfig config) throws ConfigurationException {
        try {
            endpoint = new URL(config.getEndpoint());
            query = config.getQuery();
        } catch (MalformedURLException ex) {
            throw new ConfigurationException(ex);
        }
    }

    @Override
    public SPARQLConfig newDefaultConfig() {
        return new SPARQLConfig();
    }
}
