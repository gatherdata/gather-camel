/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.example.alert;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.xml.XPathBuilder;
import org.gatherdata.camel.core.PredicateService;

/**
 * An example to illustrate the expected behavior of a Predicate
 * which evaluates whether incoming data should trigger an alert.
 * 
 */
public class ExampleAlertPredicate implements PredicateService {

    public boolean matches(Exchange exch) {
        XPathBuilder xpath = new XPathBuilder("//*[name='Sam']");
        boolean eventDetected = xpath.matches(exch);
                
        return eventDetected;
    }

}
