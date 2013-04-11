/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.delete;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 *
 * @author Alex Kreiser
 */
public class ResourceDeleteTransformer extends TransformerBase<ResourceDeleteConfig> implements ConfigDialogProvider<ResourceDeleteConfig> {

    private Logger logger = Logger.getLogger(ResourceDeleteTransformer.class);

    @Override
    protected void configureInternal(ResourceDeleteConfig config) throws ConfigurationException {
    }

    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {
            RepositoryConnection con = repository.getConnection();
            try {
                for (URI resource : config.getResources()) {
                    try {
                        con.remove(resource, null, null, graph);
                        con.remove((org.openrdf.model.Resource) null, null, resource, graph);
                        con.commit();
                    } catch (Exception ex) {
                        con.rollback();
                        logger.error("Error while deleting resource [" + resource + "]", ex);
                    }
                }
            } finally {
                con.close();
            }
        } catch (RepositoryException ex) {
            throw new TransformException(ex);
        }
    }

    @Override
    public String getName() {
        return "Resource Deletion";
    }

    @Override
    public String getDescription() {
        return "Removes all triples that have one of the configured resources as subject or object.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/impl/transform/trash.gif", application);
    }

    @Override
    public String asString() {
        return getName() + " [" + config.getResources().size() + " resources to delete]";
    }

    @Override
    public ConfigDialog getConfigDialog(ResourceDeleteConfig config) {
        return new ResourceDeleteDialog(config);
    }

    @Override
    public ResourceDeleteConfig newDefaultConfig() {
        return new ResourceDeleteConfig();
    }
}
