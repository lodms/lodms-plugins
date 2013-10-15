package at.punkt.lodms.impl.transform.replace.html;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigBeanProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 *
 * @author kreisera
 */
public class HtmlTransformer extends TransformerBase<HtmlConfig> implements ConfigBeanProvider<HtmlConfig> {

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<\\/?[a-zA-Z0-9]+[^>]*>");
    private static final String LITERAL_QUERY = "CONSTRUCT {?s ?p ?o} WHERE { ?s ?p ?o. FILTER (isLiteral(?o) && regex(str(?o),'&|<')) } ";
    
    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {
            final RepositoryConnection con = repository.getConnection();
            try {
                transformLiteral(con, graph);
            } finally {
                con.close();
            }

        } catch (Exception ex) {
            throw new TransformException(ex);
        }
    }

    @Override
    public String getName() {
        return "HTML Cleaner";
    }

    @Override
    public String getDescription() {
        return "Converts HTML entities and removes HTML tags from RDF Literals.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/impl/transform/html.gif", application);
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    protected void configureInternal(HtmlConfig config) throws ConfigurationException {
    }

    @Override
    public HtmlConfig newDefaultConfig() {
        return new HtmlConfig();
    }

    private void transformLiteral(final RepositoryConnection con, final URI graph) throws QueryEvaluationException, RepositoryException, MalformedQueryException, RDFHandlerException {
        final Collection<Statement> toRemove = new ArrayList<Statement>();
        final Collection<Statement> toAdd = new ArrayList<Statement>();
        final ValueFactory factory = con.getValueFactory();
        GraphQuery q = con.prepareGraphQuery(QueryLanguage.SPARQL, LITERAL_QUERY);
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
                if (config.isStripHtmlTags())
                    newLabel = HTML_TAG_PATTERN.matcher(newLabel).replaceAll("");
                if (config.isConvertHtmlEntities())
                    newLabel = StringEscapeUtils.unescapeHtml4(newLabel);
                if (!newLabel.equals(label)) {
                    toRemove.add(st);
                    if (value.getLanguage() != null && !value.getLanguage().isEmpty()) {
                        toAdd.add(factory.createStatement(st.getSubject(), st.getPredicate(), factory.createLiteral(newLabel, value.getLanguage())));
                    } else if (value.getDatatype() != null) {
                        toAdd.add(factory.createStatement(st.getSubject(), st.getPredicate(), factory.createLiteral(newLabel, value.getDatatype())));
                    } else {
                        toAdd.add(factory.createStatement(st.getSubject(), st.getPredicate(), factory.createLiteral(newLabel)));
                    }
                }
            }

        });
        con.remove(toRemove, graph);
        con.add(toAdd, graph);
        con.commit();
    }
}
