package org.gatherdata.camel.core;

/**
 * A WorkflowFactory creates workflows on demand. 
 * 
 */
public interface WorkflowFactory {

    Workflow createWorkflow() throws Exception;
    
}
