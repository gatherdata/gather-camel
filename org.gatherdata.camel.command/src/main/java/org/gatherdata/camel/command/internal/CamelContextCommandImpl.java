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
import org.apache.felix.shell.ShellService;
import org.apache.felix.shell.Command;
import org.gatherdata.camel.core.Workflow;
import org.gatherdata.camel.core.WorkflowService;
import org.gatherdata.camel.core.WorkflowServiceImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.google.inject.Inject;

/**
 * A command for interacting with the CamelContext.
 * 
 */
public final class CamelContextCommandImpl implements Command {
    static final transient Logger log = Logger.getLogger(CamelContextCommandImpl.class.getName());

    private final Pattern commandPattern = Pattern.compile("^(\\w+)\\s*(\\w+)\\s*(.*)");

    private final Pattern listCommandPattern = Pattern.compile("^(\\S+)");

    private final Pattern startStopCommandPattern = Pattern.compile("^(\\S+)");

    private final Pattern sendCommandPattern = Pattern.compile("^(\\S+)\\s+(.*)");

    @Inject
    Iterable<WorkflowService> workflowServices;

    public void execute(String argString, PrintStream out, PrintStream err) {
        Matcher argMatcher = commandPattern.matcher(argString);
        if (argMatcher.matches()) {
            String subCommand = argMatcher.group(2);
            String subArguments = argMatcher.group(3);

            if ("send".equals(subCommand)) {
                Matcher sendArguments = sendCommandPattern.matcher(subArguments);
                if (sendArguments.matches()) {
                    String camelContextName = sendArguments.group(1);
                    String message = sendArguments.group(2);
                    Workflow chosenWorkflow = findWorkflowContaining(camelContextName);
                    if (chosenWorkflow != null) {
                        CamelContext chosenContext = chosenWorkflow.getCamelContext();
                        ProducerTemplate producer = chosenContext.createProducerTemplate();
                        producer.sendBody(chosenWorkflow.getDefaultEndpoint(), message);
                    } else {
                        out.println("CamelContext named \"" + camelContextName + "\" not found.");
                    }
                } else {
                    out.println("send command arguments do not match expectations.");
                }
            } else if (("start").equals(subCommand)) {
                Matcher startArguments = startStopCommandPattern.matcher(subArguments);
                if (startArguments.matches()) {
                    String camelContextName = startArguments.group(1);
                    WorkflowService chosenWorkflow = findWorkflowServiceContaining(camelContextName);
                    if (chosenWorkflow != null) {
                        try {
                            chosenWorkflow.start();
                        } catch (Exception e) {
                            log.throwing(CamelContextCommandImpl.class.getName(), "start", e);
                        }
                    } else {
                        out.println("CamelContext named \"" + camelContextName + "\" not found.");
                    }
                } else {
                    for (WorkflowService wf : workflowServices) {
                        try {
                            wf.start();
                        } catch (Exception e) {
                            log.throwing(CamelContextCommandImpl.class.getName(), "start", e);
                        }

                    }
                }
            } else if (("stop").equals(subCommand)) {
                Matcher stopArguments = startStopCommandPattern.matcher(subArguments);
                if (stopArguments.matches()) {
                    String camelContextName = stopArguments.group(1);
                    WorkflowService chosenWorkflow = findWorkflowServiceContaining(camelContextName);
                    if (chosenWorkflow != null) {
                        try {
                            chosenWorkflow.stop();
                        } catch (Exception e) {
                            log.throwing(CamelContextCommandImpl.class.getName(), "stop", e);
                        }
                    } else {
                        out.println("CamelContext named \"" + camelContextName + "\" not found.");
                    }
                } else {
                    for (WorkflowService wf : workflowServices) {
                        try {
                            wf.stop();
                        } catch (Exception e) {
                            log.throwing(CamelContextCommandImpl.class.getName(), "stop", e);
                        }

                    }
                }
            } else if (("list").equals(subCommand)) {
                Matcher listMatcher = listCommandPattern.matcher(subArguments);
                if (listMatcher.matches()) {
                    String camelContextName = listMatcher.group(1);
                    CamelContext chosenContext = findCamelContextNamed(camelContextName);
                    if (chosenContext != null) {
                        for (RouteDefinition route : chosenContext.getRouteDefinitions()) {
                            out.println(route);
                        }
                    } else {
                        out.println("CamelContext named \"" + camelContextName + "\" not found.");
                    }
                } else {
                    for (WorkflowService wf : workflowServices) {
                        out.println(wf);
                    }
                }
            } else if (("help").equals(subCommand)) {
                out.println();
                out.println(getUsage());
                out.println(getLongDescription());
            }
        }
    }

    private WorkflowService findWorkflowServiceContaining(String camelContextName) {
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

    private Workflow findWorkflowContaining(String camelContextName) {
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

    private CamelContext findCamelContextNamed(String camelContextName) {
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

    public String getName() {
        return "camel";
    }
    
    public String getShortDescription() {
    	return "interact with camel routes";
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
