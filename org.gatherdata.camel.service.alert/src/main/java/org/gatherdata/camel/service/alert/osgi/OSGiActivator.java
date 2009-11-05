/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.service.alert.osgi;

import static com.google.inject.Guice.createInjector;
import static org.ops4j.peaberry.Peaberry.osgiModule;

import java.util.logging.Logger;

import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gatherdata.alert.core.spi.AlertService;
import org.gatherdata.camel.core.PredicateService;
import org.gatherdata.camel.service.alert.EventRenderingProcessor;
import org.gatherdata.camel.service.alert.NotifierProcessor;
import org.ops4j.peaberry.Export;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * OSGi bundle activator, which registers a Felix Shell command for interacting
 * with the shared CamelContext.
 */
public final class OSGiActivator
    implements BundleActivator
{
    private static final Log log = LogFactory.getLog(OSGiActivator.class);
    
    @Inject
    AlertService alertService;
    
    @Inject
    @Named(PredicateService.XML_CONTEXT)
    Export<PredicateService> xmlPredicateService;

    @Inject
    Export<EventRenderingProcessor> detectedEventRenderer;
    
    @Inject
    Export<NotifierProcessor> notifierProcessor;
    
    /**
     * Called whenever the OSGi framework starts our bundle
     */
    public void start( BundleContext bc )
        throws Exception
    {
        createInjector(osgiModule(bc), new GuiceBindingModule()).injectMembers(this);       
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc )
        throws Exception
    {
        // no need to unregister our service - the OSGi framework handles it for us
    }
}

