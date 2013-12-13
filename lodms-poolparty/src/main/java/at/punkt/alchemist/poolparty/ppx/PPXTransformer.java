/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.alchemist.poolparty.ppx;

import at.punkt.commons.openrdf.vocabulary.PPX;
import at.punkt.commons.openrdf.vocabulary.SKOS;
import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import at.punkt.poolparty.extractor.ExtractionService;
import at.punkt.poolparty.extractor.PpxClient;
import at.punkt.poolparty.extractor.web.domain.ThesaurusConcept;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import java.util.List;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

/**
 *
 * @author Kata
 */
public class PPXTransformer extends TransformerBase<PPXConfig> implements ConfigDialogProvider<PPXConfig> {

    private ValueFactory factory = new ValueFactoryImpl();
    ExtractionService extractionService;
    //     ExtractionService extractionService = new PpxClient("http://localhost:8080/extractor", "apiuser", "password");

    @Override
    protected void configureInternal(PPXConfig config) throws ConfigurationException {
    }

    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {
            extractionService =  new PpxClient(config.getServer(), "apiuser", "password");
            List<ThesaurusConcept> response = extractionService.getConcepts(config.getProjectId(), config.getLanguage(), "", config.getText());
            RepositoryConnection con = repository.getConnection();
            try {

                for (ThesaurusConcept concept : response) {
                    URI conceptUri = factory.createURI(concept.getUri());
                    Literal scoreLiteral = factory.createLiteral(concept.getScore());
                    con.add(factory.createStatement(conceptUri, PPX.SCORE, scoreLiteral), graph);

                    Literal prefLabelLiteral = factory.createLiteral(concept.getPrefLabel());
                    con.add(factory.createStatement(conceptUri, SKOS.PREFLABEL, prefLabelLiteral), graph);
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
        return "PPX";
    }

    @Override
    public String getDescription() {
        return "Enrich resource with concepts extracted by PPX.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/impl/component.png", application);
    }

    @Override
    public String asString() {
        return getName() + " [" + "text: " + config.getText() + "] [" + "numberOfConcepts: " + config.getNumberOfConcepts() + "...]";
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
