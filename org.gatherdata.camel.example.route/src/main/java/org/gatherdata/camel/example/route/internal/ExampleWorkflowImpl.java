/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
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

/**
 * Example implementation of a gather-camel RouteProvider.
 * 
 */
public final class ExampleWorkflowImpl implements WorkflowFactory {

    
    private BundleContext bundleContext;

    public ExampleWorkflowImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public Workflow createWorkflow() throws Exception {
        CamelContextFactory factory = new CamelContextFactory();
        factory.setBundleContext(bundleContext);
        DefaultCamelContext camelContext = factory.createContext();
        camelContext.setName("example-1");

        RouteBuilder myRouteBuilder = new RouteBuilder() {
            public void configure() {
                from("seda:input")
                    //.beanRef("example.camel.karaf.service.TransformService", "transform")
                    .to("log:example");

                from("timer://example?fixedRate=true&delay=10000&period=10000")
                    .setBody(constant("Hello world!"))
                    .to("seda:input");
            }
        };
        
        camelContext.addRoutes(myRouteBuilder);

        return new Workflow(camelContext, "seda:input");
    }

}
