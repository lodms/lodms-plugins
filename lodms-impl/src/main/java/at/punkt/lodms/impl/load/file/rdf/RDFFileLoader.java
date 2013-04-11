/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.load.file.rdf;

import at.punkt.lodms.impl.load.RDFHandlerLoader;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.load.LoadContext;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerWrapper;

/**
 *
 * @author Alex Kreiser
 */
public class RDFFileLoader extends RDFHandlerLoader<RDFFileConfig> implements UIComponent, ConfigDialogProvider<RDFFileConfig> {

    protected Logger logger = Logger.getLogger(RDFFileLoader.class);

    private class ClosingRDFHandlerWrapper extends RDFHandlerWrapper {

        final Writer writer;
        private final URI graph;

        public ClosingRDFHandlerWrapper(Writer writer, RDFHandler rdfHandler, URI graph) {
            super(rdfHandler);
            this.writer = writer;
            this.graph = graph;
        }

        @Override
        public void handleStatement(Statement st) throws RDFHandlerException {
            if (graph != null) {
                super.handleStatement(new ContextStatementImpl(st.getSubject(), st.getPredicate(), st.getObject(), graph));
            } else {
                super.handleStatement(st);
            }
        }

        @Override
        public void endRDF() throws RDFHandlerException {
            super.endRDF();
            try {
                writer.close();
            } catch (IOException ex) {
                logger.error("Error while closing writer after endRDF", ex);
            }
        }
    }

    @Override
    protected RDFHandler getRDFHandler(LoadContext context) throws Exception {
        FileWriter writer = new FileWriter(config.getFilePath());
        URI graph = null;
        if (config.getGraph()!= null && !config.getGraph().isEmpty())
            graph = new URIImpl(config.getGraph());
        return new ClosingRDFHandlerWrapper(writer, Rio.createWriter(RDFFormat.valueOf(config.getFormat()), writer), graph);
    }

    @Override
    public String getName() {
        return "RDF File Dump";
    }

    @Override
    public String getDescription() {
        return "Stores the RDF data in the file system in any RDF format.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/impl/load/rdffile.gif", application);
    }

    @Override
    public String asString() {
        return getName() + " [" + config.getFilePath() + "]";
    }

    @Override
    public ConfigDialog getConfigDialog(RDFFileConfig config) {
        return new RDFFileConfigDialog(config);
    }

    @Override
    protected void configureInternal(RDFFileConfig config) throws ConfigurationException {
    }

    @Override
    public RDFFileConfig newDefaultConfig() {
        return new RDFFileConfig();
    }
}