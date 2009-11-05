package org.gatherdata.camel.core;

import com.google.inject.Inject;
import com.google.inject.Injector;


/**
 * A WorkflowService uses a WorkflowFactory to dynamically control a 
 * CamelContext. 
 *
 */
public class WorkflowServiceImpl implements WorkflowService {

    protected WorkflowFactory workflowFactory;
    
    protected Workflow currentWorkflow;
    
    public WorkflowServiceImpl(WorkflowFactory workflowFactory) {
        this.workflowFactory = workflowFactory;
    }
    
    /* (non-Javadoc)
     * @see org.gatherdata.camel.context.internal.WorkflowService#getWorkflow()
     */
    public Workflow getWorkflow() {
        return currentWorkflow;
    }
    
    public WorkflowFactory getWorkflowFactory() {
        return workflowFactory;
    }

    /* (non-Javadoc)
     * @see org.gatherdata.camel.context.internal.WorkflowService#start()
     */
    public void start() throws Exception {
        if (currentWorkflow == null) {
            currentWorkflow = workflowFactory.createWorkflow();
            currentWorkflow.start();
        } else {
            currentWorkflow.startRoutes();
        }
    }

    /* (non-Javadoc)
     * @see org.gatherdata.camel.context.internal.WorkflowService#stop()
     */
    public void stop() throws Exception {
        if (currentWorkflow != null) {
            currentWorkflow.stopRoutes();
        }

    }

    /* (non-Javadoc)
     * @see org.gatherdata.camel.context.internal.WorkflowService#shutdown()
     */
    public void shutdown() throws Exception {
        currentWorkflow.stop();
        currentWorkflow = null;
    }
    
    public String toString() {
        if (currentWorkflow != null) {
            return currentWorkflow.getDefaultEndpoint().getEndpointUri() + " @ " + currentWorkflow.getCamelContext().getName();
        } else {
            return "empty workflow";
        }
    }


}
