/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.alchemist.poolparty.ppx;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import at.punkt.poolparty.api.PPTApi;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 *
 * @author Kata
 */
public class PPXTransformer extends TransformerBase<PPXConfig> implements ConfigDialogProvider<PPXConfig> {

    private final Logger logger = Logger.getLogger(PPXTransformer.class);

    @Override
    protected void configureInternal(PPXConfig config) throws ConfigurationException {
    }

    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {

            PPTApi api = new PPTApi(config.getApiConfig().getServer(), config.getApiConfig().getAuthentication());
            api.annotate(config, repository, graph, context);

        } catch (Exception ex) {
            throw new TransformException(ex);
        }
    }

    @Override
    public String getName() {
        return "PPX";
    }

    @Override
    public String getDescription() {
        return "Enrich resource with concepts extracted by PPX.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/alchemist/poolparty/pp_schirm.png", application);
    }

    @Override
    public String asString() {
        return getName() + " [" + "query: " + config.getQuery() + "] [" + "numberOfConcepts: " + config.getNumberOfConcepts() + "...]";
    }

    @Override
    public ConfigDialog getConfigDialog(PPXConfig config) {
        return new PPXConfigDialog(config);
    }

    @Override
    public PPXConfig newDefaultConfig() {
        return new PPXConfig();
    }
}
