/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.transform.replace.regex;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigBeanProvider;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 *
 * @author kreisera
 */
public class RegexTransformer extends TransformerBase<RegexConfig> implements ConfigDialogProvider<RegexConfig> {

    private static final String LITERAL_QUERY = "CONSTRUCT {?s ?p ?o} WHERE { ?s ?p ?o. FILTER (isLiteral(?o) && regex(str(?o), '%%%regex%%%')) } ";
    private static final String URI_QUERY = "SELECT DISTINCT ?uri WHERE { { ?uri ?p ?o. FILTER (isUri(?uri) && regex(str(?uri), '%%%regex%%%')) } UNION { ?s ?p ?uri. FILTER(isUri(?uri) && regex(str(?uri), '%%%regex%%%')) } }";

    @Override
    protected void configureInternal(RegexConfig config) throws ConfigurationException {
    }

    @Override
    public void transform(Repository repository, final URI graph, TransformContext context) throws TransformException {
        try {
            final RepositoryConnection con = repository.getConnection();
            if (config.getValueType() == ValueType.LITERAL) {
                try {
                    transformLiteral(con, graph);
                } finally {
                    con.close();
                }
            } else if (config.getValueType() == ValueType.URI) {
                try {
                    transformUri(con, graph);
                } finally {
                    con.close();
                }
            }

        } catch (Exception ex) {
            throw new TransformException(ex);
        }
    }

    @Override
    public String getName() {
        return "Regular Expression Transformer";
    }

    @Override
    public String getDescription() {
        return "Searches for the given regular expression within all literals and replaces it with the configured replacement";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/impl/transform/replace.png", application);
    }

    @Override
    public String asString() {
        return getName() + " [" + config.getRegex() + "]";
    }

    @Override
    public RegexConfig newDefaultConfig() {
        return new RegexConfig();
    }

    private void transformLiteral(final RepositoryConnection con, final URI graph) throws QueryEvaluationException, RepositoryException, MalformedQueryException, RDFHandlerException {
        final Collection<Statement> toRemove = new ArrayList<Statement>();
        final ValueFactory factory = con.getValueFactory();
        GraphQuery q = con.prepareGraphQuery(QueryLanguage.SPARQL, LITERAL_QUERY.replaceAll("%%%regex%%%", config.getRegex()));
        DatasetImpl enforcedDataset = new DatasetImpl();
        enforcedDataset.addDefaultGraph(graph);
        q.setDataset(enforcedDataset);
        q.evaluate(new RDFHandlerBase() {

            @Override
            public void handleStatement(Statement st) throws RDFHandlerException {
                if (!(st.getObject() instanceof Literal)) {
                    return;
                }
                Literal value = (Literal) st.getObject();
                String label = value.getLabel();
                if (label == null) {
                    return;
                }
                String newLabel = label;
                if (config.isReplaceAll()) {
                    newLabel = newLabel.replaceAll(config.getRegex(), config.getReplacement());
                } else {
                    newLabel = newLabel.replaceFirst(config.getRegex(), config.getReplacement());
                }
                if (!newLabel.equals(label)) {
                    try {
                        con.add(st.getSubject(), st.getPredicate(), value, graph);
                        toRemove.add(st);
                        if (value.getLanguage() != null && !value.getLanguage().isEmpty()) {
                            con.add(st.getSubject(), st.getPredicate(), factory.createLiteral(newLabel, value.getLanguage()), graph);
                        } else if (value.getDatatype() != null) {
                            con.add(st.getSubject(), st.getPredicate(), factory.createLiteral(newLabel, value.getDatatype()), graph);
                        } else {
                            con.add(st.getSubject(), st.getPredicate(), factory.createLiteral(newLabel), graph);
                        }
                    } catch (RepositoryException ex) {
                        throw new RDFHandlerException(ex);
                    }
                }
            }

            @Override
            public void endRDF() throws RDFHandlerException {
                try {
                    for (Statement st : toRemove) {
                        con.remove(st, graph);
                    }
                    con.commit();
                } catch (RepositoryException ex) {
                    throw new RDFHandlerException(ex);
                }
            }
        });
    }

    private void transformUri(RepositoryConnection con, URI graph) throws Exception {
        TupleQuery q = con.prepareTupleQuery(QueryLanguage.SPARQL, URI_QUERY.replaceAll("%%%regex%%%", config.getRegex()));
        DatasetImpl enforcedDataset = new DatasetImpl();
        enforcedDataset.addDefaultGraph(graph);
        q.setDataset(enforcedDataset);
        TupleQueryResult result = q.evaluate();
        final Map<URI, URI> replacements = new HashMap<URI, URI>();
        while (result.hasNext()) {
            BindingSet bSet = result.next();
            URI uri = (URI) bSet.getValue("uri");
            if (uri.toString().matches(config.getRegex())) {
                URI replacement = con.getValueFactory().createURI(uri.toString().replaceAll(config.getRegex(), config.getReplacement()));
                if (!uri.equals(replacement))
                    replacements.put(uri, replacement);
            }
        }
        result.close();
        final Collection<Statement> toRemove = new HashSet<Statement>();

        con.setAutoCommit(false);

        for (Entry<URI, URI> entry : replacements.entrySet()) {
            try {
                toRemove.clear();
                // Replace all triples with subject position
                RepositoryResult<Statement> res = con.getStatements(entry.getKey(), null, null, true, graph);
                while (res.hasNext()) {
                    Statement oldStatement = res.next();
                    toRemove.add(oldStatement);
                    con.add(entry.getValue(), oldStatement.getPredicate(), oldStatement.getObject(), graph);
                }
                res.close();
                // Replace all triples with object position
                res = con.getStatements(null, null, entry.getKey(), true, graph);
                while (res.hasNext()) {
                    Statement oldStatement = res.next();
                    toRemove.add(oldStatement);
                    con.add(oldStatement.getSubject(), oldStatement.getPredicate(), entry.getValue(), graph);
                }
                res.close();
                for (Statement remove : toRemove) {
                    con.remove(remove, graph);
                }
                con.commit();
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    @Override
    public ConfigDialog getConfigDialog(RegexConfig config) {
        return new RegexConfigDialog(config);
    }
}