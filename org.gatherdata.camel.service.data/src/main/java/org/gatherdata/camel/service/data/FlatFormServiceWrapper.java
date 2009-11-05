package org.gatherdata.camel.service.data;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * A Camel-friendly wrapper around the gather-data FlatFormService.
 * This is a marker interface for a Camel Processor which saves the
 * in-bound message in an exchange to the FlatFormService.
 * 
 */
public interface FlatFormServiceWrapper extends Processor
{
    
}

