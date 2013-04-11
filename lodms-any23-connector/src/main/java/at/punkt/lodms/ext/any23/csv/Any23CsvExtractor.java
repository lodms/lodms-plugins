/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.ext.any23.csv;

import at.punkt.lodms.ext.any23.TripleHandlerBridge;
import at.punkt.lodms.integration.ConfigBeanProvider;
import at.punkt.lodms.integration.ConfigurableBase;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.extract.ExtractContext;
import at.punkt.lodms.spi.extract.ExtractException;
import at.punkt.lodms.spi.extract.Extractor;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.deri.any23.extractor.ExtractionContext;
import org.deri.any23.extractor.ExtractionParameters;
import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.extractor.ExtractionResultImpl;
import org.deri.any23.extractor.csv.CSVExtractor;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandler;

/**
 *
 * @author Alex Kreiser
 */
public class Any23CsvExtractor extends ConfigurableBase<CsvExtractorConfig> implements Extractor, UIComponent, ConfigBeanProvider<CsvExtractorConfig> {

    private File file;
    private String baseUri;

    @Override
    public void extract(RDFHandler handler, ExtractContext context) throws ExtractException {
        try {
            CSVExtractor csvExtractor = new CSVExtractor();
            ExtractionParameters params = new ExtractionParameters(ExtractionParameters.ValidationMode.None);
            ExtractionContext ctx = new ExtractionContext("CSV", new URIImpl(baseUri));
            ExtractionResult out = new ExtractionResultImpl(ctx, csvExtractor, new TripleHandlerBridge(handler));
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            try {
                csvExtractor.run(params, ctx, in, out);
            } finally {
                in.close();
            }
        } catch (Exception ex1) {
            throw new ExtractException(ex1);
        }
    }

    @Override
    public String getName() {
        return "Any23 CSV Extractor";
    }

    @Override
    public String getDescription() {
        return "Extracts RDF from a CSV file using the Any23 framework.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/ext/any23/csv/csv.gif", application);
    }

    @Override
    public String asString() {
        return getName()+" ["+file.getAbsolutePath()+"]";
    }

    @Override
    public CsvExtractorConfig newDefaultConfig() {
        return new CsvExtractorConfig();
    }

    @Override
    protected void configureInternal(CsvExtractorConfig configBean) throws ConfigurationException {
        file = new File(configBean.getFilePath());
        baseUri = configBean.getBaseUri();
    }
}
