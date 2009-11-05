/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
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
