package org.gatherdata.camel.route.xforms.internal;

import java.net.URI;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.gatherdata.camel.core.GatherHeaders;
import org.gatherdata.commons.net.CbidFactory;

/**
 * CbidAnnotator is a {@link Processor} which annotates
 * a message with a Content Based Identifier (CBID).
 *
 */
public class CbidAnnotator implements Processor {
    
    public void process(Exchange exchange) throws Exception {
        String bodyAsText = exchange.getIn().getBody(String.class);
        URI contentURI = CbidFactory.createCbid(bodyAsText);
        
        exchange.getIn().setHeader(GatherHeaders.CBID_HEADER, contentURI);
    }

}
