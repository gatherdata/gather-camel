package org.gatherdata.camel.service.data.internal;

import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.gatherdata.camel.core.GatherHeaders;
import org.gatherdata.camel.service.data.FlatFormServiceWrapper;
import org.gatherdata.commons.net.CbidFactory;
import org.gatherdata.data.core.model.FlatForm;
import org.gatherdata.data.core.spi.FlatFormService;
import org.gatherdata.data.core.transform.xml.XmlToFlatFormTransform;
import org.joda.time.DateTime;

import com.google.inject.Inject;

/**
 * Internal implementation of the FlatFormServiceWrapper service.
 * 
 */
public final class FlatFormServiceWrapperImpl
    implements FlatFormServiceWrapper
{
    public static final Logger log = Logger.getLogger(FlatFormServiceWrapperImpl.class.getName());
    
    @Inject 
    FlatFormService archiver;
    
    XmlToFlatFormTransform transformer = new XmlToFlatFormTransform();
    
    public void process(Exchange exchange) {
        log.fine("transform, then save: " + exchange);

        String msgBody = exchange.getIn().getBody(String.class);
        FlatForm transformedMessage = transformer.transform(msgBody);
        archiver.save(transformedMessage);
    }
    
}

