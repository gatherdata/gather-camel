package org.gatherdata.camel.service.alert.internal;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gatherdata.alert.core.model.Notification;
import org.gatherdata.alert.core.spi.Notifier;
import org.gatherdata.camel.service.alert.NotifierProcessor;

import com.google.inject.Inject;

public class NotifierProcessorImpl implements NotifierProcessor {

	private static final Log log = LogFactory.getLog(NotifierProcessorImpl.class);
	
	@Inject
	Iterable<Notifier> notifiers;
	
    public void process(Exchange exchange) throws Exception {
    	boolean noticesSent = false;
    	Message inMessage = exchange.getIn();
    	Notification notice = (Notification) inMessage.getBody();
    	for (Notifier notifier : notifiers) {
    		if (notifier.canSendTo(notice.getDestination())) {
    			log.info("sending " + notice + " using " + notifier);
    			notifier.notify(notice);
    			noticesSent = true;
    			break;
    		}
    	}
    	if (!noticesSent) {
    		log.warn("No notifier found for " + notice);
    	}
    }

}
