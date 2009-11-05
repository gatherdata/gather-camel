package org.gatherdata.camel.core;


public interface WorkflowService {

    public abstract Workflow getWorkflow();

    public abstract void start() throws Exception;

    public abstract void stop() throws Exception;

    public abstract void shutdown() throws Exception;

}