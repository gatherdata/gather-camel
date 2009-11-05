package org.gatherdata.camel.core;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.model.RouteDefinition;

/**
 * A Workflow wraps a CamelContext for use by a WorkflowService. 
 *
 */
public class Workflow {

    private CamelContext camelContext;
    private String defaultEndpointUri;
    
    public Workflow(CamelContext forCamelContext, String withDefaultEndpointUri) {
        this.camelContext = forCamelContext;
        this.defaultEndpointUri = withDefaultEndpointUri;
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }
    
    public Endpoint getDefaultEndpoint() {
        return camelContext.getEndpoint(defaultEndpointUri);
    }

    public void start() throws Exception {
        camelContext.start();
    }

    public void stop() throws Exception {
        if (camelContext != null) {
            camelContext.stop();
        }
    }

    public void startRoutes() throws Exception {
        for (RouteDefinition route : camelContext.getRouteDefinitions()) {
            camelContext.startRoute(route);
        }
    }
    
    public void stopRoutes() throws Exception {
        for (RouteDefinition route : camelContext.getRouteDefinitions()) {
            camelContext.stopRoute(route);
        }
    }
    
}
