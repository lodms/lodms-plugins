package at.punkt.poolparty.api;

import at.punkt.alchemist.poolparty.ppx.PPXConfig;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.poolparty.api.json.model.Project;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.ServiceNotFoundException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.SimpleType;
import org.codehaus.jackson.type.JavaType;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerWrapper;

/**
 *
 * @author kreisera
 */
public class PPTApi {

    private final static Logger logger = Logger.getLogger(PPTApi.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String host;
    private final Authentication authentication;

    public PPTApi(String host, Authentication authentication) {
        this.host = host;
        this.authentication = authentication;
    }

    public List<Project> getProjects() throws AuthenticationFailedException, ServiceNotFoundException, Exception {
        HttpURLConnection con = getServiceConnection("api/projects");
        if (con.getResponseCode() == 401) {
            throw new AuthenticationFailedException();
        } else if (con.getResponseCode() == 404) {
            throw new ServiceNotFoundException();
        }
        JavaType type = CollectionType.construct(ArrayList.class, SimpleType.construct(Project.class));
        InputStream in = con.getInputStream();
        try {
            return (List<Project>) objectMapper.readValue(in, type);
        } finally {
            in.close();
        }
    }

    public void importRdf(String projectId, RepositoryConnection repCon, Resource sourceGraph, final Resource targetGraph) throws Exception {
        HttpURLConnection con = getServiceConnection("api/thesaurus/" + projectId + "/import");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", RDFFormat.TRIG.getDefaultMIMEType());
        try {
            RDFHandler handler = Rio.createWriter(RDFFormat.TRIG, con.getOutputStream());
            if (targetGraph != null) {
                handler = new RDFHandlerWrapper(handler) {
                    @Override
                    public void handleStatement(Statement st) throws RDFHandlerException {
                        super.handleStatement(new ContextStatementImpl(st.getSubject(), st.getPredicate(), st.getObject(), targetGraph));
                    }
                };
            }
            repCon.export(handler, sourceGraph);
            con.getOutputStream().close();
        } finally {
            con.disconnect();
        }
    }

    public void annotate(PPXConfig config, Repository repository, final URI graph, TransformContext context) throws Exception {

        Map<String, String> textMap = new HashMap<String, String>();
        textMap = getText(repository, config.getQuery(), textMap);

        for (Map.Entry<String, String> entry : textMap.entrySet()) {
            HttpURLConnection con = getServiceConnection("extractor/api/annotate");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            String documentUri = entry.getKey();
            String text = entry.getValue();
            List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            params.add(new ImmutablePair<String, String>("text", text));
            params.add(new ImmutablePair<String, String>("language", config.getLanguage()));
            params.add(new ImmutablePair<String, String>("projectId", config.getApiConfig().getProjectId()));
            params.add(new ImmutablePair<String, String>("documentUri", documentUri));
            params.add(new ImmutablePair<String, String>("numberOfConcepts", String.valueOf(config.getNumberOfConcepts())));
            params.add(new ImmutablePair<String, String>("numberOfTerms", String.valueOf(config.getNumberOfTerms())));
            params.add(new ImmutablePair<String, String>("transitiveBroaderConcepts", String.valueOf(config.isTransitiveBroaderConcepts())));
            params.add(new ImmutablePair<String, String>("transitiveBroaderTopConcepts", String.valueOf(config.isTransitiveBroaderTopConcepts())));
            params.add(new ImmutablePair<String, String>("relatedConcepts", String.valueOf(config.isRelatedConcepts())));
            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            try {
                RepositoryConnection repCon = repository.getConnection();
                try {
                    receiveResponse(con, repCon, graph);
                    repCon.commit();
                } finally {
                    repCon.close();
                }
            } finally {
                con.disconnect();
            }
        }
    }

    public static void receiveResponse(HttpURLConnection conn, RepositoryConnection repCon, URI graph)
            throws IOException, RDFParseException, RepositoryException {
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        // retrieve the response from server
        InputStream is = null;
        try {
            is = conn.getInputStream();
            repCon.add(is, "", RDFFormat.RDFXML, graph);
        } catch (IOException e) {
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String getQuery(List<Pair<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair<String, String> pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(pair.getKey());
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private Map<String, String> getText(Repository repository, String q, Map<String, String> textMap) {
        try {
            RepositoryConnection repConn = repository.getConnection();
            try {
                TupleQuery query = repConn.prepareTupleQuery(
                        QueryLanguage.SPARQL, q);
                TupleQueryResult result = query.evaluate();
                while (result.hasNext()) {
                    BindingSet bindSet = result.next();
                    String documentUri = bindSet.getValue("documentUri").stringValue();
                    String text = bindSet.getValue("text").stringValue();
                    if (!textMap.containsKey(documentUri)) {
                        textMap.put(documentUri, text);
                    } else {
                        String t = textMap.get(documentUri);
                        String concatenatedText = t + " " + text;
                        textMap.put(documentUri, concatenatedText);
                    }
                }
            } finally {
                repConn.close();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return textMap;
    }

    public void createSnapshot(String projectId) throws Exception {
        HttpURLConnection con = getServiceConnection("api/thesaurus/" + projectId + "/snapshot");
        try {
            if (con.getResponseCode() != 200) {
                throw new Exception("PPT API returned response code: " + con.getResponseCode());
            }
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection getServiceConnection(String path) throws IOException {
        URL url = getServiceUrl(host, path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        authentication.visit(con);
        return con;
    }

    public static URL getServiceUrl(String host, String path) {
        if (!host.endsWith("/")) {
            host += "/";
        }
        try {
            return new URL(host + path);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
