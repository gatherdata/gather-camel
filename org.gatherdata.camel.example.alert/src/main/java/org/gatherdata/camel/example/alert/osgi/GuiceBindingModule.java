package org.gatherdata.camel.example.alert.osgi;

import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.Attributes.properties;
import static org.ops4j.peaberry.util.TypeLiterals.export;
import static org.ops4j.peaberry.util.TypeLiterals.iterable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gatherdata.alert.core.spi.AlertService;
import org.gatherdata.camel.core.PredicateService;
import org.gatherdata.camel.example.alert.ExampleAlertPredicate;
import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.Import;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import java.util.Properties;

public class GuiceBindingModule extends AbstractModule {
    Log log = LogFactory.getLog(GuiceBindingModule.class);
    
    @Override
    protected void configure() {

        Properties serviceAttrs = new Properties();
        serviceAttrs.put(Constants.SERVICE_RANKING, new Long(0));
        serviceAttrs.put(Constants.SERVICE_DESCRIPTION, "Example PredicateService for evaluating text/xml data");
        bind(export(PredicateService.class))
            .annotatedWith(Names.named("text/xml"))
            .toProvider(service(new ExampleAlertPredicate())
                .attributes(properties(serviceAttrs))
                .export());

    }

    
}
