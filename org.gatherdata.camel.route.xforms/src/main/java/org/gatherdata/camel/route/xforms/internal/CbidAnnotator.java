/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
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
