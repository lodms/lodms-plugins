package at.punkt.poolparty.api;

import java.net.URLConnection;

/**
 *
 * @author kreisera
 */
public interface Authentication {

    public void visit(URLConnection con);
    public AuthType getType();
}
