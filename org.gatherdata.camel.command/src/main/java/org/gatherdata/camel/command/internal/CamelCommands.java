/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.command.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.model.RouteDefinition;
import org.gatherdata.camel.core.Workflow;
import org.gatherdata.camel.core.WorkflowService;
import org.gatherdata.camel.core.WorkflowServiceImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.command.CommandSession;

import com.google.inject.Inject;

/**
 * Sends data to a camel context.
 * 
 */
public class CamelCommands {
    static final transient Logger log = Logger.getLogger(CamelCommands.class.getName());

    @Inject
    Iterable<WorkflowService> workflowServices;

    public void send(CommandSession session, String args[]) {

        if ((args != null) && (args.length > 1)) {
            String camelContextName = args[0];
            String message = args[1];
            Workflow chosenWorkflow = findWorkflowContaining(camelContextName);
            if (chosenWorkflow != null) {
                CamelContext chosenContext = chosenWorkflow.getCamelContext();
                ProducerTemplate producer = chosenContext.createProducerTemplate();
                producer.sendBody(chosenWorkflow.getDefaultEndpoint(), message);
            } else {
                System.out.println("CamelContext named \"" + camelContextName + "\" not found.");
            }
        } else {
            System.out.println("usage: camel:send <context> <message> - sends a message into a context");
        }
    }

    public void list(CommandSession session, String args[]) {

        if ((args != null) && (args.length > 0)) {
            String camelContextName = args[0];
            CamelContext chosenContext = findCamelContextNamed(camelContextName);
            if (chosenContext != null) {
                for (RouteDefinition route : chosenContext.getRouteDefinitions()) {
                    System.out.println(route);
                }
            } else {
                System.out.println("CamelContext named \"" + camelContextName + "\" not found.");
            }
        } else {
            for (WorkflowService wf : workflowServices) {
                System.out.println(wf);
            }
        }
    }

    public void start(CommandSession session, String args[]) {

        if ((args != null) && (args.length > 0)) {
            String camelContextName = args[0];
            WorkflowService chosenWorkflow = findWorkflowServiceContaining(camelContextName);
            if (chosenWorkflow != null) {
                try {
                    chosenWorkflow.start();
                } catch (Exception e) {
                    log.throwing(CamelCommands.class.getName(), "start", e);
                }
            } else {
                System.out.println("CamelContext named \"" + camelContextName + "\" not found.");
            }
        } else {
            for (WorkflowService wf : workflowServices) {
                try {
                    wf.start();
                } catch (Exception e) {
                    log.throwing(CamelCommands.class.getName(), "start", e);
                }
            }
        }
    }
    
    public void stop(CommandSession session, String args[]) {

        if ((args != null) && (args.length > 0)) {
            String camelContextName = args[0];
            WorkflowService chosenWorkflow = findWorkflowServiceContaining(camelContextName);
            if (chosenWorkflow != null) {
                try {
                    chosenWorkflow.stop();
                } catch (Exception e) {
                    log.throwing(CamelCommands.class.getName(), "stop", e);
                }
            } else {
                System.out.println("CamelContext named \"" + camelContextName + "\" not found.");
            }
        } else {
            for (WorkflowService wf : workflowServices) {
                try {
                    wf.stop();
                } catch (Exception e) {
                    log.throwing(CamelCommands.class.getName(), "stop", e);
                }
            }
        }
    }

    protected WorkflowService findWorkflowServiceContaining(String camelContextName) {
        WorkflowService foundService = null;
        for (WorkflowService possibleService : workflowServices) {
            CamelContext possibleContext = possibleService.getWorkflow().getCamelContext();
            if (camelContextName.equals(possibleContext.getName())) {
                foundService = possibleService;
                break;
            }
        }
        return foundService;
    }

    protected Workflow findWorkflowContaining(String camelContextName) {
        Workflow foundWorkflow = null;
        for (WorkflowService wf : workflowServices) {
            CamelContext possibleContext = wf.getWorkflow().getCamelContext();
            if (camelContextName.equals(possibleContext.getName())) {
                foundWorkflow = wf.getWorkflow();
                break;
            }
        }
        return foundWorkflow;
    }

    protected CamelContext findCamelContextNamed(String camelContextName) {
        CamelContext foundContext = null;
        for (WorkflowService wf : workflowServices) {
            CamelContext possibleContext = wf.getWorkflow().getCamelContext();
            if (camelContextName.equals(possibleContext.getName())) {
                foundContext = possibleContext;
                break;
            }
        }
        return foundContext;
    }
    
    public String getLongDescription() {
        StringBuffer description = new StringBuffer();
        description.append("\tlist [context]  - list contexts, or routes if context specified\n");
        description.append("\tstart <context> - starts a camel context\n");
        description.append("\tstop <context>  - stops a running camel context\n");
        description.append("\tsend <context> <message> - sends a message into a context\n");
        return description.toString();
    }

    public String getUsage() {
        return "camel <subcommand> [<args> ...]";
    }

}
