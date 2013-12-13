package at.punkt.alchemist.poolparty.transform;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResultHandlerBase;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author kreisera
 */
public class DBPediaDataTransformer extends TransformerBase<DBPediaDataConfig> implements ConfigDialogProvider<DBPediaDataConfig> {

    private final Logger logger = Logger.getLogger(DBPediaDataTransformer.class);

    @Override
    protected void configureInternal(DBPediaDataConfig config) throws ConfigurationException {
    }

    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {
            RepositoryConnection con = repository.getConnection();
            final HashSet<String> links = new HashSet<String>();
            final HashMap<String, Set<String>> mapping = new HashMap<String, Set<String>>();
            try {
                con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT ?concept ?link WHERE { ?concept ?property ?link. "
                        + "FILTER ( (?property = <http://www.w3.org/2004/02/skos/core#exactMatch> || ?property = <http://www.w3.org/2004/02/skos/core#closeMatch> "
                        + "|| ?property = <http://www.w3.org/2004/02/skos/core#relatedMatch> || ?property = <http://www.w3.org/2004/02/skos/core#broadMatch> "
                        + "|| ?property = <http://www.w3.org/2004/02/skos/core#narrowMatch> || ?property = <http://www.w3.org/2002/07/owl#sameAs> "
                        + "|| ?property = <http://www.w3.org/2000/01/rdf-schema#seeAlso>) && regex(str(?link), \"^http://dbpedia.org\") ) "
                        + "} ").evaluate(new TupleQueryResultHandlerBase() {

                    @Override
                    public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
                        links.add(bindingSet.getValue("link").stringValue());
                        Set<String> m = mapping.get(bindingSet.getValue("link").stringValue());
                        if (m == null) {
                            m = new HashSet<String>();
                            mapping.put(bindingSet.getValue("link").stringValue(), m);
                        }
                        m.add(bindingSet.getValue("concept").stringValue());
                    }
                });
                logger.info(links.size() + " links found in local cache - running data queries");
                con.setAutoCommit(false);
                for (String link : links) {
                    try {
                        for (String concept : mapping.get(link)) {
                            String query = URLEncoder.encode(DBPediaProperty.toQuery(config.getProperties(), concept, link), "UTF-8");
                            URL url = new URL("http://lod.semantic-web.at/sparql?default-graph-uri="+URLEncoder.encode(config.getGraph(), "UTF-8") +"&format=application/rdf%2Bxml&query=" + query);
                            con.add(url, "", RDFFormat.RDFXML, graph);
                            con.commit();
                        }
                    } catch (Exception ex) {
                        context.getWarnings().add(ex.getMessage());
                    }
                }
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            throw new TransformException(ex);
        }
    }

    @Override
    public String getName() {
        return "DBPedia Data";
    }

    @Override
    public String getDescription() {
        return "Fetches data from DBPedia for every link.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/alchemist/poolparty/dbpedia.png", application);
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    public ConfigDialog getConfigDialog(DBPediaDataConfig config) {
        return new DBPediaDataDialog(config);
    }

    @Override
    public DBPediaDataConfig newDefaultConfig() {
        return new DBPediaDataConfig();
    }
}
