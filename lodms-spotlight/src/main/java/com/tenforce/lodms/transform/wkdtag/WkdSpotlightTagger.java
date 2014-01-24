package com.tenforce.lodms.transform.wkdtag;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigDialog;
import at.punkt.lodms.integration.ConfigDialogProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.vaadin.Application;
import com.vaadin.terminal.Resource;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WkdSpotlightTagger extends TransformerBase<WkdSpotlightTaggerConfig> implements ConfigDialogProvider<WkdSpotlightTaggerConfig> {
    private Logger log = Logger.getLogger(WkdSpotlightTagger.class);
    private ValueFactory valueFactory = ValueFactoryImpl.getInstance();
    private final URI rdfType = valueFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private final URI pebblesSuggestion = valueFactory.createURI("http://pebbles.wolterskluwer.de/Suggestion");
    private final URI skosRelated = valueFactory.createURI("http://www.w3.org/2004/02/skos/core#related");




    /**
     * Returns a new {@link at.punkt.lodms.integration.ConfigDialog} instance that will be embedded in the
     * dialog window on configuration of this component.
     *
     * @param config An already existing configuration object<br/>
     *               {@code null} if this is the first configuration of the component
     * @return
     */
    @Override
    public ConfigDialog getConfigDialog(WkdSpotlightTaggerConfig config) {
        return new WkdSpotlightTaggerConfigDialog(config);
    }

    /**
     * Returns a new (blank) JavaBean instance with its default values set.
     *
     * @return
     */
    @Override
    public WkdSpotlightTaggerConfig newDefaultConfig() {
        return new WkdSpotlightTaggerConfig();
    }

    @Override
    protected void configureInternal(WkdSpotlightTaggerConfig config) throws ConfigurationException {

    }

    /**
     * Transforms the cached RDF data in the repository.
     *
     * @param repository The repository where the RDF data is cached that should be transformed
     * @param graph      The graph that contains the RDF data which was extracted
     * @param context    The context containing meta information about this transformation process
     * @throws at.punkt.lodms.spi.transform.TransformException
     *          If the transformation fails, this exception has to be thrown
     */
    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        try {
            WKDLawHandler wkdLawHandler = new WKDLawHandler(repository);
            org.openrdf.model.Resource documentResource = wkdLawHandler.getDocumentResource(graph);
            String suggestionURI = documentResource + "?Suggestions";
            context.getCustomData().put("wkdspotlighttagger.documenturi",suggestionURI);
            List<SpotlightAnnotation> annotationList = getAnnotations(repository, graph, context);
            RepositoryConnection connection = repository.getConnection();
            connection.clear(graph);
            connection.commit();
            URI suggestionGraph = valueFactory.createURI(suggestionURI);
            connection.add(suggestionGraph,rdfType, pebblesSuggestion,graph);
            try {
                for (SpotlightAnnotation annotation : annotationList) {
                    URI fragmentURI = annotation.getFragmentURI();
                    for (SpotlightResource resource : annotation.getResources()) {
                        URI relatedConcept = valueFactory.createURI(resource.getURI());
                        connection.add(documentResource,skosRelated,relatedConcept,graph);
                        connection.add(fragmentURI,skosRelated,relatedConcept,graph);
                    }
                }
                connection.commit();
            }
            finally {
                connection.close();
            }
        }
        catch (Exception e) {
            throw new TransformException(e.getMessage(),e);
        }
    }

    private List<SpotlightAnnotation> getAnnotations(Repository repository, URI graph, TransformContext context) throws RepositoryException, QueryEvaluationException, TransformException {
        List<SpotlightAnnotation> annotations = new ArrayList<SpotlightAnnotation>();
        WKDLawHandler wkdLawHandler = new WKDLawHandler(repository);
        QueryResult<BindingSet> result = wkdLawHandler.getTextValues(graph);
        while (result.hasNext()) {
            BindingSet bSet = result.next();
            Value text = bSet.getValue("value");
            URI fragment = (URI) bSet.getValue("fragment");
            SpotlightAnnotation annotation = getTagsFor(fragment,text.stringValue());
            annotations.add(annotation);
        }
        return  annotations;
    }


    private SpotlightAnnotation getTagsFor(URI fragment,String text) throws TransformException {
        RestTemplate rest = new RestTemplate();
        CommonsClientHttpRequestFactory clientHttpRequestFactory = new CommonsClientHttpRequestFactory();
        clientHttpRequestFactory.setReadTimeout(10000);
        rest.setRequestFactory(clientHttpRequestFactory);
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        converters.add(new MappingJacksonHttpMessageConverter());
        converters.add(new FormHttpMessageConverter());
        rest.setMessageConverters(converters);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("prefix", fragment.toString()+"#");
        map.add("text", text.toString());
        HttpEntity<?> httpEntity = new HttpEntity<Object>(map, getHttpHeaders());
        try {
            SpotlightAnnotation annotation = rest.postForObject(config.getSpotlightUrl(), httpEntity, SpotlightAnnotation.class);
            annotation.setFragmentURI(fragment);
            return annotation;
        }
        catch (RestClientException e) {
            log.warn("could not request tags for: " + text);
            log.warn(e.getMessage());
            return new SpotlightAnnotation();
        }
    }

    public static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.<MediaType>asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }
    /**
     * Returns a short, self-descriptive name of the component.
     *
     * @return
     */
    @Override
    public String getName() {
        return "WKD Spotlight Tagger";
    }

    /**
     * Returns a description of what functionality this component provides.
     *
     * @return
     */
    @Override
    public String getDescription() {
        return "Request tags for text in rdf:value from spotlight";
    }

    /**
     * Returns an icon as vaadin {@link com.vaadin.terminal.Resource}, {@code null} if no icon is available.
     *
     * @param application
     * @return
     */
    @Override
    public Resource getIcon(Application application) {
        return null;
    }

    /**
     * Returns a string representing the configured internal state of this component.<br/>
     * This will be used to display this component after having been configured.
     *
     * @return
     */
    @Override
    public String asString() {
        return this.getName() + ": " + config.getSpotlightUrl();
    }
}
