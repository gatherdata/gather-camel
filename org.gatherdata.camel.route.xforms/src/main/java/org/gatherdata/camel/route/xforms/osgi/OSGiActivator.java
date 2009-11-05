package org.gatherdata.camel.route.xforms.osgi;

import static com.google.inject.Guice.createInjector;
import static org.ops4j.peaberry.Peaberry.osgiModule;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.ops4j.peaberry.Export;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.gatherdata.camel.core.Workflow;
import org.gatherdata.camel.core.WorkflowFactory;
import org.gatherdata.camel.core.WorkflowService;
import org.gatherdata.camel.core.WorkflowServiceImpl;
import org.gatherdata.camel.route.xforms.internal.XFormsWorkflowImpl;
import org.gatherdata.camel.service.archive.ArchiveServiceWrapper;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Extension of the default OSGi bundle activator
 */
public final class OSGiActivator
    implements BundleActivator
{
    @Inject
    private ArchiveServiceWrapper archiveService;
    

    @Inject
    Export<WorkflowService> workflowService;
    
    /**
     * Called whenever the OSGi framework starts our bundle
     */
    public void start( BundleContext bc )
        throws Exception
    {
        Injector guicer = createInjector(osgiModule(bc), new GuiceBindingModule(bc));
        guicer.injectMembers(this);
        workflowService.get().start();
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
        workflowService.get().stop();
    }

}

