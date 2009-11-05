package org.gatherdata.camel.service.archive;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * A Camel-friendly wrapper around the gather-archiver ArchiverService.
 * This is a marker interface for a Camel Processor which saves the
 * in-bound message in an exchange to the ArchiverService.
 * 
 */
public interface ArchiveServiceWrapper extends Processor
{
    
}

