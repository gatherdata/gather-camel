package org.gatherdata.camel.service.archive.internal;

import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.gatherdata.archiver.core.model.GatherArchive;
import org.gatherdata.archiver.core.model.MutableGatherArchive;
import org.gatherdata.archiver.core.spi.ArchiverService;
import org.gatherdata.camel.core.GatherHeaders;
import org.gatherdata.camel.service.archive.ArchiveServiceWrapper;
import org.gatherdata.commons.net.CbidFactory;
import org.joda.time.DateTime;

import com.google.inject.Inject;

/**
 * Internal implementation of the ArchiveServiceWrapper service.
 * 
 */
public final class ArchiveServiceWrapperImpl
    implements ArchiveServiceWrapper
{
    public static final Logger log = Logger.getLogger(ArchiveServiceWrapperImpl.class.getName());
    
    @Inject 
    ArchiverService archiver;
    
    public void process(Exchange exchange) {
        log.fine("saving: " + exchange);
        GatherArchive transformedMessage = transform(exchange.getIn());
        archiver.save(transformedMessage);
    }

    protected GatherArchive transform(Message msg) {
        MutableGatherArchive msgAsArchive = new MutableGatherArchive();
        
        String msgBody = msg.getBody(String.class);
        msgAsArchive.setContent(msgBody);
        msgAsArchive.setDateCreated(new DateTime());
        
        URI uidHeader= (URI) msg.getHeader(GatherHeaders.CBID_HEADER);
        if (uidHeader == null) {
            uidHeader = CbidFactory.createCbid(msgBody);
        }
        msgAsArchive.setUid(uidHeader);
        

        Map<String, String> metadata = msgAsArchive.getMetadata();
        for(String headerKey : msg.getHeaders().keySet()) {
            Object headerValue =  msg.getHeader(headerKey);
            if (headerValue != null) {
                metadata.put(headerKey, headerValue.toString());
            }
        }
        log.log(Level.FINE, "transform, stored metadata: " + msgAsArchive.getMetadata());
        return msgAsArchive;
    }
    
}

