/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.example.http.internal;

import static com.google.inject.Guice.createInjector;
import static org.ops4j.peaberry.Peaberry.osgiModule;
import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.Attributes.properties;
import static org.ops4j.peaberry.util.TypeLiterals.export;
import static org.ops4j.peaberry.util.TypeLiterals.iterable;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.gatherdata.camel.core.WorkflowService;
import org.gatherdata.camel.http.ServletProxyProducer;
import org.gatherdata.camel.http.internal.ServletProxyProducerImpl;
import org.ops4j.peaberry.Import;
import org.ops4j.peaberry.util.AbstractWatcher;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Extension of the default OSGi bundle activator
 */
public final class ExampleActivator
    implements BundleActivator
{

    private static final Logger log = Logger.getLogger(ExampleActivator.class.getName());
    
    @Inject
    HttpService httpService;
    
    @Inject
    ServletProxyProducer camelProxy;
    
    PostMessageServlet postServlet;
    
    /**
     * Called when the OSGi framework starts our bundle
     */
    @SuppressWarnings( "unchecked" )
    public void start( BundleContext bc )
        throws Exception
    {
        postServlet = new PostMessageServlet();
        
        Injector guicer = createInjector(osgiModule(bc), new AbstractModule() {
            @Override
            protected void configure() {
                bind(ServletProxyProducer.class)
                    .toProvider(service(ServletProxyProducer.class).single());
                
                bind(HttpService.class)
                .toProvider(service(HttpService.class)
                        .out(new AbstractWatcher<HttpService>() {
                            @Override
                            protected HttpService adding(Import<HttpService> service) {
                             // the returned object is used in the modified and removed calls
                                HttpService instance = service.get();
                                
                                // create a default context to share between registrations
                                final HttpContext httpContext = instance.createDefaultHttpContext();
                                // register the hello world servlet
                                final Dictionary initParams = new Hashtable();
                                initParams.put( "from", "HttpService" );
                                try {
                                    instance.registerServlet(
                                        "/example/spi",             // alias
                                        postServlet,   // registered servlet
                                        initParams,                 // init params
                                        httpContext                 // http context
                                    );
                                } catch (ServletException e) {
                                    e.printStackTrace();
                                } catch (NamespaceException e) {
                                    e.printStackTrace();
                                }

                             return instance;
                            }

                        })
                        .single());

            }

        }
        );
        
        guicer.injectMembers(this);
        guicer.injectMembers(postServlet);

    }

    /**
     * Called when the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
    }

}

