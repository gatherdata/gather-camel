package org.gatherdata.camel.service.alert.internal;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.model.language.LanguageExpression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gatherdata.alert.core.model.DetectedEvent;
import org.gatherdata.alert.core.model.LanguageScript;
import org.gatherdata.alert.core.model.impl.MutableDetectedEvent;
import org.gatherdata.alert.core.model.RuleSet;
import org.gatherdata.alert.core.spi.AlertService;
import org.gatherdata.alert.core.spi.EventDetector;
import org.gatherdata.camel.core.GatherHeaders;
import org.gatherdata.camel.core.PredicateService;
import org.gatherdata.camel.service.alert.AlertExchangeProperties;
import org.joda.time.DateTime;

import com.google.inject.Inject;

public class AlertServicePredicate implements PredicateService, EventDetector {
    
    private static Log log = LogFactory.getLog(AlertServicePredicate.class);
    
    @Inject
    AlertService alertService;
    
    private String context;
    
    public AlertServicePredicate() {
        this.context = XML_CONTEXT;
    }
    
    public AlertServicePredicate(String context) {
        this.context = context;
    }

    public boolean matches(Exchange exchange) {
        boolean doesMatch = false;
        if (alertService != null) {
            Set<DetectedEvent> detectedEvents = new HashSet<DetectedEvent>();
            Iterable<RuleSet> rulesets = alertService.getActiveRulesetsFor(context);
            DateTime commonDateOfDetection = new DateTime();
            URI subjectUid = (URI) exchange.getIn().getHeader(GatherHeaders.CBID_HEADER);
            for (RuleSet rule : rulesets) {
                if (detect(rule, exchange)) {
                    // create DetectedEvent, add to collection
                    log.info("event detected for plan: " + rule.getPlan());
                    detectedEvents.add(MutableDetectedEvent.createFor(commonDateOfDetection, subjectUid, rule));
                    doesMatch = true;
                }
            }
            if (detectedEvents.size() > 0) {
                exchange.setProperty(AlertExchangeProperties.DETECTED_EVENTS_PROPERTY, detectedEvents);
            }
        } else {
            log.warn("missing AlertService, can not perform matching");
        }
        
        return doesMatch;
    }

    public Iterable<DetectedEvent> detect(Iterable<RuleSet> rules, Map<String, Object> map) {
        
        // TODO Auto-generated method stub
        return null;
    }
    
    public boolean detect(RuleSet rule, Exchange exch) {
        boolean anyMatch = false;
        boolean allMatch = true;
        
        for (LanguageScript script : rule.getPredicates()) {
            LanguageExpression lexp = new LanguageExpression(script.getLanguage(),script.getScript());
            boolean doesMatch = lexp.matches(exch);
            anyMatch |= doesMatch;
            allMatch &= doesMatch;
        }
        
        allMatch &= anyMatch;
        
        return (rule.isSatisfyAll() ? allMatch : anyMatch);
    }

}
