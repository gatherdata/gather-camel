package org.gatherdata.camel.service.alert.osgi;

import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.Attributes.properties;
import static org.ops4j.peaberry.util.TypeLiterals.export;
import static org.ops4j.peaberry.util.TypeLiterals.iterable;

import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gatherdata.alert.core.spi.AlertService;
import org.gatherdata.alert.core.spi.Notifier;
import org.gatherdata.alert.core.spi.TemplateRenderer;
import org.gatherdata.camel.core.PredicateService;
import org.gatherdata.camel.service.alert.EventRenderingProcessor;
import org.gatherdata.camel.service.alert.NotifierProcessor;
import org.gatherdata.camel.service.alert.internal.AlertServicePredicate;
import org.gatherdata.camel.service.alert.internal.EventRenderingProcessorImpl;
import org.gatherdata.camel.service.alert.internal.NotifierProcessorImpl;
import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.Import;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import java.util.Properties;

public class GuiceBindingModule extends AbstractModule {
    private static final String PREDICATE_CONTEXT = "predicate context";
    
    @Override
    protected void configure() {
        
        bind(AlertService.class).toProvider(service(AlertService.class).single());

        // used to parameterize construction of AlertServicePredicate
        bind(String.class).annotatedWith(Names.named(PREDICATE_CONTEXT)).toInstance(PredicateService.XML_CONTEXT);
        
        bind(iterable(TemplateRenderer.class)).toProvider(service(TemplateRenderer.class).multiple());
        
        bind(iterable(Notifier.class)).toProvider(service(Notifier.class).multiple());
        
        Properties serviceAttrs = new Properties();
        serviceAttrs.put(Constants.SERVICE_RANKING, new Long(10));
        serviceAttrs.put(Constants.SERVICE_DESCRIPTION, "PredicateService for evaluating text/xml data");
        bind(export(PredicateService.class))
            .annotatedWith(Names.named(PredicateService.XML_CONTEXT))
            .toProvider(service(AlertServicePredicate.class)
                .attributes(properties(serviceAttrs))
                .export()
                );
        
        serviceAttrs = new Properties();
        serviceAttrs.put(Constants.SERVICE_RANKING, new Long(10));
        serviceAttrs.put(Constants.SERVICE_DESCRIPTION, "Renders DetectedEvents according to PlannedNotification.");
        bind(export(EventRenderingProcessor.class))
            .toProvider(service(EventRenderingProcessorImpl.class)
                .attributes(properties(serviceAttrs))
                .export()
                );
        
        serviceAttrs = new Properties();
        serviceAttrs.put(Constants.SERVICE_RANKING, new Long(10));
        serviceAttrs.put(Constants.SERVICE_DESCRIPTION, "Sends out notifications using registered Notifiers.");
        bind(export(NotifierProcessor.class))
            .toProvider(service(NotifierProcessorImpl.class)
                .attributes(properties(serviceAttrs))
                .export()
                );

    }

    @Provides
    @Singleton
    AlertServicePredicate provideAlertServicePredicate(Injector guicer, @Named(PREDICATE_CONTEXT) String context) {
        AlertServicePredicate providedInstance = new AlertServicePredicate(context);
        guicer.injectMembers(providedInstance);
        return providedInstance;
    }
}
