package at.punkt.poolparty.api;

import java.net.URLConnection;
import sun.misc.BASE64Encoder;

/**
 *
 * @author kreisera
 */
public class BasicAuthentication extends UsernamePasswordCredentials {

    public BasicAuthentication() {
        super();
    }

    public BasicAuthentication(String username, String password) {
        super(username, password);
    }
    
    @Override
    public void visit(URLConnection con) {
        String credentials = getUsername() + ":" + getPassword();
        String encodedCredentials = new BASE64Encoder().encode(credentials.getBytes());
        con.setRequestProperty("Authorization", "Basic " + encodedCredentials);
    }

    @Override
    public AuthType getType() {
        return AuthType.Basic_Auth;
    }
}
