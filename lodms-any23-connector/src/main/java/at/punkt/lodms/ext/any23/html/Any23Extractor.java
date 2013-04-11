/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.ext.any23.html;

import at.punkt.lodms.ext.any23.TripleHandlerBridge;
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
import java.util.ArrayList;
import java.util.List;
import org.deri.any23.Any23;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.HTTPDocumentSource;
import org.openrdf.rio.RDFHandler;

/**
 *
 * @author Alex Kreiser
 */
public class Any23Extractor extends ConfigurableBase<Any23Config> implements Extractor, UIComponent, ConfigDialogProvider<Any23Config> {

    private final Any23 runner = new Any23();
    private List<DocumentSource> sources = new ArrayList<DocumentSource>();
    private String encoding = "UTF-8";

    public Any23Extractor() {
        runner.setHTTPUserAgent("Any23 Agent");
    }
    
    @Override
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
        TripleHandlerBridge bridge = new TripleHandlerBridge(handler);
        try {
            for (DocumentSource source : sources) {
                runner.extract(source, bridge);
            }
        } catch (Exception ex) {
            throw new ExtractException(ex);
        }
    }

    @Override
    public String getName() {
        return "Any23 HTML Extractor";
    }

    @Override
    public String getDescription() {
        return "Extracts RDFa, Microformats, Microdata and Metadata from HTML pages.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/ext/any23/html/html.gif", application);
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    public ConfigDialog getConfigDialog(Any23Config config) {
        return new Any23Dialog(config);
    }

    public Any23 getRunner() {
        return runner;
    }

    public List<DocumentSource> getSources() {
        return sources;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    protected void configureInternal(Any23Config config) throws ConfigurationException {
        try {
            for (Any23Document doc : config.getDocuments()) {
                DocumentSource source = new HTTPDocumentSource(runner.getHTTPClient(), doc.getUrl());
                sources.add(source);
            }
        } catch (Exception ex) {
            throw new ConfigurationException(ex);
        }
    }

    @Override
    public Any23Config newDefaultConfig() {
        return new Any23Config();
    }
}