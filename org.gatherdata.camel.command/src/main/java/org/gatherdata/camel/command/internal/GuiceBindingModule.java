/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.command.internal;

import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.Attributes.properties;
import static org.ops4j.peaberry.util.TypeLiterals.export;
import static org.ops4j.peaberry.util.TypeLiterals.iterable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gatherdata.camel.core.WorkflowService;
import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.Import;
import org.osgi.framework.Constants;
import org.osgi.service.command.CommandProcessor;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

import java.util.Properties;

public class GuiceBindingModule extends AbstractModule {
    Log log = LogFactory.getLog(GuiceBindingModule.class);
    
    @Override
    protected void configure() {
        // import all WorkflowServices
        bind(iterable(WorkflowService.class)).toProvider(service(WorkflowService.class).multiple());
        
        // export the shell commands
        Properties commandAttrs = new Properties();
        commandAttrs.put(CommandProcessor.COMMAND_SCOPE, "camel");
        commandAttrs.put(CommandProcessor.COMMAND_FUNCTION, new String[] { "send", "list", "start", "stop" });
//        bind(export(CamelCommands.class)).toProvider(
//                service(CamelCommands.class).attributes(properties(commandAttrs))
//                .export());
        
    }
    
}
