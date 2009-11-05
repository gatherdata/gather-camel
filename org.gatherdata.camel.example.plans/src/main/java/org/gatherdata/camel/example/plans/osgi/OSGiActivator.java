/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.example.plans.osgi;

import static com.google.inject.Guice.createInjector;
import static org.gatherdata.alert.builder.LanguageScriptBuilder.expressedIn;
import static org.gatherdata.alert.builder.RuleSetBuilder.rules;
import static org.gatherdata.alert.builder.PlannedNotificationBuilder.address;
import static org.ops4j.peaberry.Peaberry.osgiModule;

import java.util.logging.Logger;

import org.gatherdata.alert.builder.ActionPlanBuilder;
import org.gatherdata.alert.core.spi.AlertService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.Inject;

/**
 * OSGi bundle activator, which registers a Felix Shell command for interacting
 * with the shared CamelContext.
 */
public final class OSGiActivator
    implements BundleActivator
{
    private static final transient Logger log = Logger.getLogger(OSGiActivator.class.getName());
    
    @Inject
    AlertService alertService;
    
    /**
     * Called whenever the OSGi framework starts our bundle
     */
    public void start( BundleContext bc )
        throws Exception
    {
        createInjector(osgiModule(bc), new GuiceBindingModule()).injectMembers(this);
        
        alertService.save(ActionPlanBuilder.plan()
                .named("whereIsSam")
                .describedAs("looks for Sam elements in XML")
                .applyingRules(
                        rules("text/xml")
                            .rule(expressedIn("xpath").script("//*[name='Sam']"))
                )
                .notifying(
                        address("mailto:sysadmin@kollegger.name")
                            .message(expressedIn("vm").script("test gather-alert message"))
                )
                .build());
        
        alertService.save(ActionPlanBuilder.plan()
                .named("samInTheHouse")
                .describedAs("Sam is in the house")
                .applyingRules(
                        rules("text/xml")
                            .rule(expressedIn("xpath").script("//*[name='Sam']"))
                )
                .notifying(
                        address("mailto:sysadmin@kollegger.name")
                            .message(expressedIn("vm").script("detected event: $event"))
                )

                .build());
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

