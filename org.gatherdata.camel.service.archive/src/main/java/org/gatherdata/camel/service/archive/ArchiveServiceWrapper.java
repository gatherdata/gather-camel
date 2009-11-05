/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
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

