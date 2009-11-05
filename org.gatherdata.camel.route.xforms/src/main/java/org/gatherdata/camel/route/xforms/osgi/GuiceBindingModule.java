package org.gatherdata.camel.route.xforms.osgi;

import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.Attributes.properties;
import static org.ops4j.peaberry.util.TypeLiterals.export;
import static org.ops4j.peaberry.util.TypeLiterals.iterable;

import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gatherdata.camel.core.PredicateService;
import org.gatherdata.camel.core.WorkflowFactory;
import org.gatherdata.camel.core.WorkflowService;
import org.gatherdata.camel.core.WorkflowServiceImpl;
import org.gatherdata.camel.route.xforms.XFormsWorkflow;
import org.gatherdata.camel.route.xforms.internal.XFormsWorkflowImpl;
import org.gatherdata.camel.service.alert.EventRenderingProcessor;
import org.gatherdata.camel.service.alert.NotifierProcessor;
import org.gatherdata.camel.service.archive.ArchiveServiceWrapper;
import org.gatherdata.camel.service.archive.internal.ArchiveServiceWrapperImpl;
import org.gatherdata.camel.service.data.FlatFormServiceWrapper;
import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.Import;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import java.util.Properties;

public class GuiceBindingModule extends AbstractModule {

    private BundleContext bc;

    public GuiceBindingModule(BundleContext bc) {
        this.bc = bc;
    }

    @Override
    protected void configure() {
        // imports
        bind(ArchiveServiceWrapper.class)
            .toProvider(service(ArchiveServiceWrapper.class).single());
        
        bind(FlatFormServiceWrapper.class)
        	.toProvider(service(FlatFormServiceWrapper.class).single());

        bind(PredicateService.class).annotatedWith(Names.named(PredicateService.XML_CONTEXT))
            .toProvider(service(PredicateService.class).single());
        
        bind(EventRenderingProcessor.class)
            .toProvider(service(EventRenderingProcessor.class).single());
        
        bind(NotifierProcessor.class)
        	.toProvider(service(NotifierProcessor.class).single());

        // exports
        Properties serviceAttrs = new Properties();
        serviceAttrs.put(Constants.SERVICE_RANKING, new Long(100));
        serviceAttrs.put(Constants.SERVICE_DESCRIPTION, "Gather camel-powered workflow for Xforms handling");
        bind(export(WorkflowService.class)).toProvider(
                service(WorkflowServiceImpl.class).attributes(properties(serviceAttrs)).export());
        bind(WorkflowFactory.class).annotatedWith(Names.named("xforms")).toInstance(new XFormsWorkflowImpl(bc));
    }

    @Provides
    protected WorkflowServiceImpl createWorkflowService(@Named("xforms") WorkflowFactory factory) {
        return new WorkflowServiceImpl(factory);

    }
    
}
