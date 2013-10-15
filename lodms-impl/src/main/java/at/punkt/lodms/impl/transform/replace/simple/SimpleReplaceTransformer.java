package at.punkt.lodms.impl.transform.replace.simple;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 *
 * @author kreisera
 */
public class SimpleReplaceTransformer extends TransformerBase<SimpleReplaceConfig> implements ConfigDialogProvider<SimpleReplaceConfig> {

    private final ValueFactory factory = ValueFactoryImpl.getInstance();

    @Override
    protected void configureInternal(SimpleReplaceConfig config) throws ConfigurationException {
    }

    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {
            RepositoryConnection con = repository.getConnection();
            Set<Statement> toRemove = new HashSet<Statement>();
            Set<Statement> toAdd = new HashSet<Statement>();
            try {
                ArrayList<Statement> replaceStmts = new ArrayList<Statement>();
                for (URI triggerProp : config.getTriggerProperties()) {
                    replaceStmts.clear();
                    RepositoryResult<Statement> result = con.getStatements(null, triggerProp, null, true, graph);
                    result.addTo(replaceStmts);
                    result.close();
                    for (Statement replace : replaceStmts) {
                        if (config.getObjectType().isAssignableFrom(replace.getObject().getClass())) {
                            toRemove.add(replace);
                            toAdd.add(factory.createStatement(replace.getSubject(), replace.getPredicate(), transformValue(replace.getObject())));
                        }
                    }
                }
                con.remove(toRemove, graph);
                con.add(toAdd, graph);
                con.commit();
            } finally {
                con.close();
            }
        } catch (RepositoryException ex) {
            throw new TransformException(ex);
        }
    }

    @Override
    public String getName() {
        return "Simple Replace Transformer";
    }

    @Override
    public String getDescription() {
        return "Replaces objects in RDF statements based on a fixed replacement list.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/impl/transform/replace.png", application);
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    public ConfigDialog getConfigDialog(SimpleReplaceConfig config) {
        return new SimpleReplaceDialog(config);
    }

    @Override
    public SimpleReplaceConfig newDefaultConfig() {
        return new SimpleReplaceConfig();
    }

    private Value transformValue(Value oldValue) {
        String oldValueString = oldValue.stringValue();
        if (oldValue instanceof Literal) {
            oldValueString = ((Literal) oldValue).getLabel();
        }
        if (config.getReplacements().containsKey(oldValueString)) {
            if (config.getReplacementType().equals(URI.class)) {
                return factory.createURI(config.getReplacements().getProperty(oldValueString));
            } else if (config.getReplacementType().equals(Literal.class)) {
                return factory.createLiteral(config.getReplacements().getProperty(oldValueString));
            }
        }
        return oldValue;
    }
}
