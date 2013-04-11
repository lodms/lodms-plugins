/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.sparql.update;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

/**
 *
 * @author kreisera
 */
public class SPARQLUpdateTransformer extends TransformerBase<SPARQLUpdateTransformConfig> implements ConfigDialogProvider<SPARQLUpdateTransformConfig> {

    @Override
    protected void configureInternal(SPARQLUpdateTransformConfig config) throws ConfigurationException {

    }

    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {
            RepositoryConnection con = repository.getConnection();
            try {
                con.setAutoCommit(false);
                con.prepareUpdate(QueryLanguage.SPARQL, config.getQuery()).execute();
                con.commit();
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            throw new TransformException(ex);
        }
    }

    @Override
    public String getName() {
        return "SPARQL Update Query Transformer";
    }

    @Override
    public String getDescription() {
        return "Transforms RDF data based on a SPARQL update query.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/impl/component.png", application);
    }

    @Override
    public String asString() {
        String query = this.config.getQuery();
        if (query != null && query.length() > 50)
            query = query.substring(0, 50);
        return getName()+ " ["+query+"]";
    }

    @Override
    public ConfigDialog getConfigDialog(SPARQLUpdateTransformConfig config) {
        return new SPARQLUpdateTransformDialog(config);
    }

    @Override
    public SPARQLUpdateTransformConfig newDefaultConfig() {
        return new SPARQLUpdateTransformConfig();
    }

}
