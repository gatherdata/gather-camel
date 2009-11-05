package org.gatherdata.camel.service.archive.osgi;

import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.Attributes.properties;
import static org.ops4j.peaberry.util.TypeLiterals.export;
import static org.ops4j.peaberry.util.TypeLiterals.iterable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gatherdata.archiver.core.spi.ArchiverService;
import org.gatherdata.camel.service.archive.ArchiveServiceWrapper;
import org.gatherdata.camel.service.archive.internal.ArchiveServiceWrapperImpl;
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
        
        bind(ArchiverService.class).toProvider(service(ArchiverService.class).single());
    
        // export the ArchiveServiceWrapper
        Properties serviceAttrs = new Properties();
        serviceAttrs.put(Constants.SERVICE_RANKING, new Long(100));
        serviceAttrs.put(Constants.SERVICE_DESCRIPTION, "Camel-friendly wrapper for gather-archiver ArchiverService");
        bind(export(ArchiveServiceWrapper.class))
            .toProvider(service(new ArchiveServiceWrapperImpl())
                .attributes(properties(serviceAttrs))
                .export());

    }

    
}
