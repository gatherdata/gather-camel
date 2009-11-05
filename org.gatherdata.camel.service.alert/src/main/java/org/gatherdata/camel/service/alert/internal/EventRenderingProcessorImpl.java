package org.gatherdata.camel.service.alert.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gatherdata.alert.core.model.DetectedEvent;
import org.gatherdata.alert.core.model.impl.MutableNotification;
import org.gatherdata.alert.core.model.Notification;
import org.gatherdata.alert.core.model.PlannedNotification;
import org.gatherdata.alert.core.spi.AlertService;
import org.gatherdata.alert.core.spi.TemplateRenderer;
import org.gatherdata.camel.service.alert.EventRenderingProcessor;

import com.google.inject.Inject;

public class EventRenderingProcessorImpl implements EventRenderingProcessor {

    private static final Log log = LogFactory.getLog(EventRenderingProcessorImpl.class);

    @Inject
    AlertService alertService;

    @Inject
    Iterable<TemplateRenderer> renderers;
    
    public void process(Exchange exchange) throws Exception {
    	Message inMessage = exchange.getIn();
    	List<Notification> notifications = new ArrayList<Notification>();
        try {
            DetectedEvent detectedEvent = (DetectedEvent) inMessage.getBody();
            log.debug("rendering: " + detectedEvent);
            for (PlannedNotification plannedNotification : detectedEvent.getDetectedBy().getNotifications()) {
            	boolean wasRendered = false;
                String templateType = plannedNotification.getTemplate().getLanguage();
                String template = plannedNotification.getTemplate().getScript();
                for (TemplateRenderer renderer : renderers) {
					if (renderer.canRender(templateType)) {
						MutableNotification notice = new MutableNotification();
						notice.setDestination(plannedNotification.getDestination());
						notice.setMessage(renderer.render(template, bindAttributes(exchange)));
					    notifications.add(notice);
                        wasRendered  = true;
                        break;
                    }
                }
                if (!wasRendered) {
                    notifications.add(new MutableNotification(plannedNotification.getDestination(), detectedEvent.toString()));
                }
            }
        } catch (ClassCastException cce) {
            log.error("Can only render DetectedEvent objects", cce);
        }
        inMessage.setBody(notifications);

    }

    private Map<String, Object> bindAttributes(Exchange exchange) {
        Map<String, Object> boundMap = new HashMap<String, Object>();
        Message inMessage = exchange.getIn();

        boundMap.putAll(exchange.getProperties());
        boundMap.put("event", inMessage.getBody());
        
        return boundMap;
    }

}
