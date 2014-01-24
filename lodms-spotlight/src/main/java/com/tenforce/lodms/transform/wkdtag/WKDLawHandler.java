package com.tenforce.lodms.transform.wkdtag;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.util.List;

/*
 * This class abstracts knowledge of how a law is stored in RDF
 * It provides utility methods to request specific law metadata attributes
 */
public class WKDLawHandler {
    private Repository repository;
    final String valueQuery="prefix bibo:<http://purl.org/ontology/bibo/>\n" +
            "     prefix metalex:<http://www.metalex.eu/metalex/2008-05-02#>\n" +
            "            \n" +
            "            SELECT ?law  ?fragment ?value\n" +
            "            WHERE  {\n" +
            "             GRAPH ?graph { \n" +
            "               { {?law a bibo:Legislation} UNION {?law a bibo:LegalDecision} } .\n" +
            "               ?law metalex:fragment  ?fragment .\n" +
            "               ?fragment  rdf:value ?value .\n" +
            "                }    \n" +
            "            }";

    public WKDLawHandler(Repository repository) {
        this.repository = repository;
    }

    /*
     * determine document resource uri for a given datasetURI
     * will fall back to datasetURI if no document resource uri could be found
     */
    public Resource getDocumentResource(URI datasetURI) throws RepositoryException {
        RepositoryConnection connection = repository.getConnection();
        try {
            RepositoryResult<Statement> statements =  connection.getStatements(null, ValueFactoryImpl.getInstance().createURI("http://www.metalex.eu/metalex/2008-05-02#fragment"), null, false, datasetURI);
            List<Statement> statementList = statements.asList();

            if (statementList.isEmpty()) {
                return datasetURI;
            }

            org.openrdf.model.Resource subject = statementList.get(0).getSubject();
            statements.close();
            return subject;
        }
        finally {
            connection.close();
        }
    }


    public QueryResult<BindingSet> getTextValues(URI datasetURI) throws  RepositoryException {
        RepositoryConnection connection = repository.getConnection();
        try {
            TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, valueQuery);
            query.setBinding("graph",datasetURI);
            QueryResult<BindingSet> result = query.evaluate();
            System.out.println(result.hasNext());
            return result;
        }
        catch (QueryEvaluationException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
        catch (MalformedQueryException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
        finally {
//            connection.close();
        }
    }

}
