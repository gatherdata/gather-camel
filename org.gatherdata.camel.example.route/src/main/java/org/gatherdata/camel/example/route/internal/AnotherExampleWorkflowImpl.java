package org.gatherdata.camel.example.route.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.osgi.CamelContextFactory;
import org.gatherdata.camel.core.Workflow;
import org.gatherdata.camel.core.WorkflowFactory;
import org.osgi.framework.BundleContext;

import com.google.inject.Inject;

/**
 * Example implementation of a gather-camel RouteProvider.
 * 
 */
public final class AnotherExampleWorkflowImpl implements WorkflowFactory {

    
    private BundleContext bundleContext;

    public AnotherExampleWorkflowImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public Workflow createWorkflow() throws Exception {
        CamelContextFactory factory = new CamelContextFactory();
        factory.setBundleContext(bundleContext);
        DefaultCamelContext camelContext = factory.createContext();
        camelContext.setName("example-2");

        RouteBuilder myRouteBuilder = new RouteBuilder() {
            public void configure() {
                from("seda:input") // same name, but not the same endpoint! because seda is thread-local
                    //.beanRef("example.camel.karaf.service.TransformService", "transform")
                    .to("log:example2"); // sending to a different log category

                from("timer://anotherExample?fixedRate=true&delay=10000&period=10000")
                    .setBody(constant("Hello World, from the Department of Redundancy Department!"))
                    .to("seda:input");
            }
        };
        
        camelContext.addRoutes(myRouteBuilder);

        return new Workflow(camelContext, "seda:input");
    }

}
