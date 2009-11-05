package org.gatherdata.camel.http.internal;

import static com.google.inject.Guice.createInjector;
import static org.ops4j.peaberry.Peaberry.osgiModule;
import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.Attributes.properties;
import static org.ops4j.peaberry.util.TypeLiterals.export;
import static org.ops4j.peaberry.util.TypeLiterals.iterable;

import java.util.Properties;

import org.gatherdata.camel.core.WorkflowService;
import org.gatherdata.camel.http.ServletProxyProducer;
import org.ops4j.peaberry.Export;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

/**
 * OSGi bundle activator, which registers a Felix Shell command for interacting
 * with the shared CamelContext.
 */
public final class OSGiActivator
    implements BundleActivator
{
        
    @Inject
    Export<ServletProxyProducer> servletProxyProducer;
    
    /**
     * Called whenever the OSGi framework starts our bundle
     */
    public void start( BundleContext bc )
        throws Exception
    {
        createInjector(osgiModule(bc), new AbstractModule() {
            @Override
            protected void configure() {
                // import all WorkflowServices
                bind(iterable(WorkflowService.class)).toProvider(service(WorkflowService.class).multiple());
                
                // export the ServletProxyProducer
                
                Properties serviceAttrs = new Properties();
                serviceAttrs.put(Constants.SERVICE_RANKING, new Long(100));
                serviceAttrs.put(Constants.SERVICE_PID, ServletProxyProducer.class.getName());
                bind(export(ServletProxyProducer.class))
                    .toProvider(service(new ServletProxyProducerImpl())
                        .attributes(properties(serviceAttrs))
                        .export());
                        
            }

        }
        ).injectMembers(this);
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
        // no need to unregister our service - the OSGi framework handles it for us
    }
}

