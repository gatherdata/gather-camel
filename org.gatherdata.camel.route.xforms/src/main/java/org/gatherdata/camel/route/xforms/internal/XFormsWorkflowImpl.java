/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.route.xforms.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.osgi.CamelContextFactory;
import org.apache.camel.processor.interceptor.Tracer;
import org.gatherdata.camel.core.PredicateService;
import org.gatherdata.camel.core.Workflow;
import org.gatherdata.camel.core.WorkflowFactory;
import org.gatherdata.camel.route.xforms.XFormsWorkflow;
import org.gatherdata.camel.service.alert.AlertExchangeProperties;
import org.gatherdata.camel.service.alert.EventRenderingProcessor;
import org.gatherdata.camel.service.alert.NotifierProcessor;
import org.gatherdata.camel.service.archive.ArchiveServiceWrapper;
import org.gatherdata.camel.service.data.FlatFormServiceWrapper;
import org.osgi.framework.BundleContext;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Workflow for handling XForms. 
 * 
 */
public final class XFormsWorkflowImpl implements XFormsWorkflow, WorkflowFactory {

    Logger log = Logger.getLogger(XFormsWorkflowImpl.class.getName());
    
    
    private BundleContext bundleContext;
    
    @Inject
    private ArchiveServiceWrapper toArchivalStorage;
    
    @Inject
    private FlatFormServiceWrapper toFlatFormStorage;
    
    @Inject
    @Named(PredicateService.XML_CONTEXT)
    private PredicateService xmlAlertPredicate;

    @Inject
    private EventRenderingProcessor detectedEventRenderer;
    
    @Inject 
    private NotifierProcessor notifierProcessor; 
    
    public XFormsWorkflowImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public Workflow createWorkflow() throws Exception {
        
        if (toArchivalStorage == null) {
            throw new NullPointerException("ArchiveServiceWrapper not injected");
        }
        
        CamelContextFactory factory = new CamelContextFactory();
        factory.setBundleContext(bundleContext);
        DefaultCamelContext camelContext = factory.createContext();
        camelContext.setName("xforms-workflow");
        
        RouteBuilder myRouteBuilder = new RouteBuilder() {
            public void configure() {
                Tracer tracer = new Tracer();
                tracer.setLogStackTrace(true);
                tracer.getDefaultTraceFormatter().setShowBreadCrumb(false);
                getContext().addInterceptStrategy(tracer);

                Processor annotateCbid = new CbidAnnotator();
                
                from("file:" + FILE_INPUT_DIRECTORY + "?include=(.*)\\.xml")
                    .to(XFORMS_INPUT_QUEUE);
                
                from(XFORMS_INPUT_QUEUE)
                    .process(annotateCbid)
                    .multicast()
                        .to(
                                ARCHIVE_DIRECTORY_ENDPOINT,
                                ARCHIVE_QUEUE,
                                ALERT_QUEUE
                                )
                    .process(toFlatFormStorage)
                    .to("log:xforms-workflow?level=DEBUG&showHeaders=true")
                    ;
                
                from(ARCHIVE_QUEUE)
                    .process(toArchivalStorage)
                    ;

                from(ALERT_QUEUE)
                    .choice()
                        .when(header(Exchange.CONTENT_TYPE).isEqualTo("text/xml"))
                            .filter(xmlAlertPredicate) // detect event using predicates, a collection of DetectedEvents will be added to exchange
                                .split(property(AlertExchangeProperties.DETECTED_EVENTS_PROPERTY)) // split out the detected events
                                .to(PREPARE_NOTICES_QUEUE) // forward each detected event to the notification queue
                            ;
                
                from(PREPARE_NOTICES_QUEUE) // expects message-in body to be DetectedEvents
                    .process(detectedEventRenderer) // replace body with rendering of PlannedNotification for detected EventType
                    .split(body()) // split out notifications
                    .multicast()
                    	.to(
                    		"direct:notice-files",
                    		SEND_NOTICES_QUEUE
                            )
                    ;
                
                from("direct:notice-files") // expects message-in body to be Notifications
                	.process(new Processor() {
                		public void process(Exchange exchange) {
                			Message in = exchange.getIn();
                			in.setBody(in.getBody(String.class));
                		}
                	}).to(NOTIFICATION_DIRECTORY_ENDPOINT)
                	;
                
                from(SEND_NOTICES_QUEUE)
                	.process(notifierProcessor)
                	;
            }
        };
        
        camelContext.addRoutes(myRouteBuilder);

        return new Workflow(camelContext, XFORMS_INPUT_QUEUE);
    }

}
