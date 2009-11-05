package org.gatherdata.camel.example.route.internal;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import org.gatherdata.camel.core.Workflow;
import org.gatherdata.camel.core.WorkflowFactory;
import org.gatherdata.camel.core.WorkflowService;
import org.gatherdata.camel.core.WorkflowServiceImpl;

/**
 * Extension of the default OSGi bundle activator
 */
public final class ExampleActivator
    implements BundleActivator
{
    private WorkflowService workflowService;
    private WorkflowService anotherWorkflowService;

    
    /**
     * Called whenever the OSGi framework starts our bundle
     */
    public void start( BundleContext bc )
        throws Exception
    {
        Dictionary props = new Properties();
        // add specific service properties here...

        WorkflowFactory exampleFactory = new ExampleWorkflowImpl(bc);
        workflowService = new WorkflowServiceImpl(exampleFactory);
        
        /**
         * Having another WorkflowSerice within the same bundle is wasteful,
         * since one service can hold all the routes you need.
         * But, this does illustrate what it looks like at runtime and enables
         * control over two different groups of routes.
         */
        WorkflowFactory anotherExampleFactory = new AnotherExampleWorkflowImpl(bc);
        anotherWorkflowService = new WorkflowServiceImpl(anotherExampleFactory);
        
        // Register our example service implementation in the OSGi service registry
        bc.registerService( WorkflowService.class.getName(), workflowService, props );
        bc.registerService( WorkflowService.class.getName(), anotherWorkflowService, props );
        
        // Start the workflow to create the contexts, then immediately stop them from doing anything...
        // TODO: consider adding an "init" operation which would have the same effect. 
        workflowService.start();
        anotherWorkflowService.start();
        workflowService.stop();
        anotherWorkflowService.stop();
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
        workflowService.shutdown();
        anotherWorkflowService.shutdown();
    }
}

