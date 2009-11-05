package org.gatherdata.camel.example.alert;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ExampleNotifier implements Processor {

    public void process(Exchange exch) throws Exception {
        System.out.println("notice sent");
    }

}
