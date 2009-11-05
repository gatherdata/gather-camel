/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.service.data.osgi;

import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.Attributes.properties;
import static org.ops4j.peaberry.util.TypeLiterals.export;
import static org.ops4j.peaberry.util.TypeLiterals.iterable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gatherdata.camel.service.data.FlatFormServiceWrapper;
import org.gatherdata.camel.service.data.internal.FlatFormServiceWrapperImpl;
import org.gatherdata.data.core.spi.FlatFormService;
import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.Import;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

import java.util.Properties;

public class GuiceBindingModule extends AbstractModule {
    Log log = LogFactory.getLog(GuiceBindingModule.class);
    
    @Override
    protected void configure() {
        
        bind(FlatFormService.class).toProvider(service(FlatFormService.class).single());
    
        // export the ArchiveServiceWrapper
        Properties serviceAttrs = new Properties();
        serviceAttrs.put(Constants.SERVICE_RANKING, new Long(100));
        serviceAttrs.put(Constants.SERVICE_DESCRIPTION, "Camel-friendly wrapper for gather-data FlatFormService");
        bind(export(FlatFormServiceWrapper.class))
            .toProvider(service(new FlatFormServiceWrapperImpl())
                .attributes(properties(serviceAttrs))
                .export());

    }

    
}
