package org.gatherdata.camel.route.xforms;

public interface XFormsWorkflow {
    
    /**
     * ABKTODO: make much of this configurable
     */
    //public static final String XFORMS_INPUT_QUEUE = "activemq:topic:xforms-input";
    public static final String XFORMS_INPUT_QUEUE = "vm:xforms-input";
    
    public static final String ARCHIVE_QUEUE = "vm:to-be-archived";
    
    public static final String ALERT_QUEUE = "vm:evaluate-for-alert";
    
    public static final String PREPARE_NOTICES_QUEUE = "vm:prepare-notices";
    
    public static final String SEND_NOTICES_QUEUE = "vm:send-notices";
    
    public static final String FILE_INPUT_DIRECTORY = "gather/data/input"; 
    
    public static final String ARCHIVE_DIRECTORY_ENDPOINT = "file:gather/data/archive"; 

    public static final String NOTIFICATION_DIRECTORY_ENDPOINT = "file:gather/notification?fileName=${date:now:yyyyMMddHHmmssSSS}.txt"; 

    public static final String EVENT_TO_NOTIFICATION_TRANSFORM = "DetectedEventRenderer";

}
