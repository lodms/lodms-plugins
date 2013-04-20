/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.punkt.lodms.impl.load.dummy;

import at.punkt.lodms.integration.UIComponent;
import at.punkt.lodms.spi.load.LoadContext;
import at.punkt.lodms.spi.load.LoadException;
import at.punkt.lodms.spi.load.Loader;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;


/**
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public class DummyLoader implements Loader, UIComponent {

    @Override
    public void load(Repository rpstr, URI uri, LoadContext lc) throws LoadException {
        try {
            RepositoryConnection con = rpstr.getConnection();
            try {
                con.export(new RDFXMLPrettyWriter(System.out), uri);
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            throw new LoadException(ex);
        }
    }

    @Override
    public String getName() {
        return "Dummy Loader";
    }

    @Override
    public String getDescription() {
        return "Prints Stuff to System.out";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/at/punkt/lodms/impl/load/console.png", application);
    }

    @Override
    public String asString() {
        return getName();
    }
}