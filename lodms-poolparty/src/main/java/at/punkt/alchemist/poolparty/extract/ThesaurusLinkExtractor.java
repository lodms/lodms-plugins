package at.punkt.alchemist.poolparty.extract;

import at.punkt.lodms.integration.*;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.ExtractException;
import at.punkt.lodms.spi.extract.Extractor;
import at.punkt.poolparty.api.PPTApi;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.apache.log4j.Logger;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;

/**
 *
 * @author kreisera
 */
public class ThesaurusLinkExtractor extends ConfigurableBase<ThesaurusLinkConfig> implements Extractor, UIComponent, ConfigDialogProvider<ThesaurusLinkConfig>{

    private final Logger logger = Logger.getLogger(ThesaurusLinkExtractor.class);
    private final static String query = "CONSTRUCT {}";

    @Override
    protected void configureInternal(ThesaurusLinkConfig config) throws ConfigurationException {
    }

    @Override
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("PREFIX skos:<http://www.w3.org/2004/02/skos/core#> CONSTRUCT {?concept <").append(config.getLinkProperty()).append("> ?link } WHERE { ?concept a skos:Concept. ");
            queryBuilder.append("?concept <").append(config.getLinkProperty()).append("> ?link. }");
            logger.info(queryBuilder.toString());
            URL url = PPTApi.getServiceUrl(config.getApiConfig().getServer(), "PoolParty/sparql/"+config.getApiConfig().getUriSupplement()+"?format=application/rdf%2Bxml&query="+URLEncoder.encode(queryBuilder.toString(), "UTF-8"));
            URLConnection connection = url.openConnection();
            config.getApiConfig().getAuthentication().visit(connection);
            RDFParser parser = Rio.createParser(RDFFormat.RDFXML);
            parser.setRDFHandler(handler);
            parser.parse(connection.getInputStream(), "");
        } catch (Exception ex) {
            throw new ExtractException(ex);
        }
    }

    @Override
    public String getName() {
        return "PoolParty Thesaurus Links";
    }

    @Override
    public String getDescription() {
        return "Fetches all links from thesaurus concepts to external linked data sources.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/alchemist/poolparty/pp_schirm.png", application);
    }

    @Override
    public String asString() {
        return getName()+" ["+config.getApiConfig().getServer()+" | "+config.getApiConfig().getProjectId()+"]";
    }

    @Override
    public ConfigDialog getConfigDialog(ThesaurusLinkConfig config) {
        return new ThesaurusLinkDialog(config);
    }

    @Override
    public ThesaurusLinkConfig newDefaultConfig() {
        return new ThesaurusLinkConfig();
    }
}
