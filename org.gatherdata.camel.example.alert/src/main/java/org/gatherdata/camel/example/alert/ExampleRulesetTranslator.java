/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.example.alert;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.gatherdata.alert.core.model.DetectedEvent;
import org.gatherdata.alert.core.model.impl.MutableDetectedEvent;

public class ExampleRulesetTranslator implements Processor {

    public void process(Exchange exch) throws Exception {
        Message in = exch.getIn();
        DetectedEvent detectedEvent = new MutableDetectedEvent();
        exch.setProperty("detected-event", detectedEvent);
        
        System.out.println("DetectedEvent attached to exchange");
    }

}
