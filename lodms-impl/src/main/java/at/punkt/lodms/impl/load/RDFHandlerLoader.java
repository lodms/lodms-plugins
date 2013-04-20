/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.load;

import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.spi.load.LoadContext;
import at.punkt.lodms.spi.load.LoadException;
import at.punkt.lodms.spi.load.Loader;
import java.io.Serializable;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFHandler;

/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class RDFHandlerLoader<T extends Serializable> extends ConfigurableBase<T> implements Loader {

    @Override
    public void load(Repository repository, URI graph, LoadContext context) throws LoadException {
        try {
            RepositoryConnection con = repository.getConnection();
            try {
                con.export(getRDFHandler(context), graph);
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            throw new LoadException("Unable to load data", ex);
        }
        
    }
    
    protected abstract RDFHandler getRDFHandler(LoadContext context) throws Exception;
}
