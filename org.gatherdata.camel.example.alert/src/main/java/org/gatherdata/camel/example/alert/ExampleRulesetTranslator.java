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
