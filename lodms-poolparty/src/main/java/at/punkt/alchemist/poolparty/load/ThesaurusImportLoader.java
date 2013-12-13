package at.punkt.alchemist.poolparty.load;

import at.punkt.lodms.base.LoaderBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.load.LoadContext;
import at.punkt.lodms.spi.load.LoadException;
import at.punkt.poolparty.api.PPTApi;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.springframework.util.StringUtils;

/**
 *
 * @author kreisera
 */
public class ThesaurusImportLoader extends LoaderBase<ThesaurusImportConfig> implements ConfigDialogProvider<ThesaurusImportConfig> {

    private final Logger logger = Logger.getLogger(ThesaurusImportLoader.class);

    @Override
    protected void configureInternal(ThesaurusImportConfig config) throws ConfigurationException {
    }

    @Override
    public String getName() {
        return "Thesaurus Import";
    }

    @Override
    public String getDescription() {
        return "Imports the data into a PoolParty thesaurus via PoolParty API.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/alchemist/poolparty/pp_schirm.png", application);
    }

    @Override
    public String asString() {
        return getName() + " [" + config.getApiConfig().getServer() + " | " + config.getApiConfig().getProjectId() + "]";
    }

    @Override
    public void load(Repository repository, URI graph, LoadContext context) throws LoadException {
        try {
            PPTApi api = new PPTApi(config.getApiConfig().getServer(), config.getApiConfig().getAuthentication());
            RepositoryConnection repCon = repository.getConnection();
            try {
                org.openrdf.model.Resource targetGraph = null;
                if (StringUtils.hasText(config.getGraph())) {
                    targetGraph = (org.openrdf.model.Resource) new URIImpl(config.getGraph());
                }
                api.importRdf(config.getApiConfig().getProjectId(), repCon, graph, targetGraph);
                api.createSnapshot(config.getApiConfig().getProjectId());
            } finally {
                repCon.close();
            }
        } catch (Exception ex) {
            throw new LoadException(ex);
        }
    }

    @Override
    public ConfigDialog getConfigDialog(ThesaurusImportConfig config) {
        return new ThesaurusImportDialog(config);
    }

    @Override
    public ThesaurusImportConfig newDefaultConfig() {
        return new ThesaurusImportConfig();
    }
}
