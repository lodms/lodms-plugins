/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.load;

import at.punkt.lodms.spi.load.LoadContext;
import java.io.Serializable;
import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class RepositoryImporter<T extends Serializable> extends RDFHandlerLoader<T> {

    protected Repository repository;
    protected Resource graph;
    protected boolean clearBefore;

    public Resource getGraph() {
        return graph;
    }

    public void setGraph(Resource graph) {
        this.graph = graph;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public boolean isClearBefore() {
        return clearBefore;
    }

    public void setClearBefore(boolean clearBefore) {
        this.clearBefore = clearBefore;
    }

    @Override
    protected RDFHandler getRDFHandler(LoadContext context) throws Exception {
        final RepositoryConnection con = repository.getConnection();
        con.setAutoCommit(false);
        if (clearBefore && graph != null) {
            con.clear(graph);
            con.commit();
        }
        RDFInserter inserter = new RDFInserter(con) {

            @Override
            public void endRDF() throws RDFHandlerException {
                super.endRDF();
                try {
                    con.commit();
                } catch (RepositoryException ex) {
                    throw new RDFHandlerException(ex);
                } finally {
                    try {
                        con.close();
                    } catch (RepositoryException ex) {
                        throw new RDFHandlerException(ex);
                    }
                }
            }
        };
        if (graph != null) {
            inserter.enforceContext(graph);
        }
        return inserter;
    }
}
