package org.gatherdata.camel.http.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.gatherdata.camel.core.Workflow;
import org.gatherdata.camel.core.WorkflowService;
import org.gatherdata.camel.http.ServletProxyProducer;

import com.google.inject.Inject;

public class ServletProxyProducerImpl implements ServletProxyProducer {

    private static final Logger log = Logger.getLogger(ServletProxyProducerImpl.class.getName());
    
    @Inject
    Iterable<WorkflowService> workflowServices;

    /* (non-Javadoc)
     * @see org.gatherdata.camel.http.ServletProxyProducer#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
     */
    public void sendMessage(HttpServletRequest request, HttpServletResponse response, String toCamelContextName) {
        Workflow chosenWorkflow = findWorkflowContaining(toCamelContextName);
        if (chosenWorkflow != null) {
            CamelContext chosenContext = chosenWorkflow.getCamelContext();
            ProducerTemplate producer = chosenContext.createProducerTemplate();
            Map<String, Object> headers = extractHeaders(request);
            Object body;
            try {
                body = readBody(request);
            } catch (Exception e) {
                e.printStackTrace();
                body = e;
            }
            producer.sendBodyAndHeaders(chosenWorkflow.getDefaultEndpoint(), body, headers);
        } else {
            log.warning("CamelContext named \"" + toCamelContextName + "\" not found.");
        }
    }
    
    private Object readBody(HttpServletRequest request) throws IOException {
        byte[] rawBytes = IOUtils.toByteArray(request.getInputStream());
        return rawBytes;
    }

    private Map<String, Object> extractHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new HashMap<String, Object>();
        
        String contentType = "";
        //apply the headerFilterStrategy
        Enumeration names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            Object value = request.getHeader(name);
            // mapping the content-type 
            if (name.toLowerCase().equals("content-type")) {
                name = Exchange.CONTENT_TYPE;
                contentType = (String) value;                
            }
            headers.put(name, value);
        }

        //we populate the http request parameters for GET and POST 
        String method = request.getMethod();
        if (method.equalsIgnoreCase("GET") || (method.equalsIgnoreCase("POST") && contentType.equalsIgnoreCase("application/x-www-form-urlencoded"))) {
            names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                Object value = request.getParameter(name);
                headers.put(name, value);
            }
        }
        
        // store the method and query and other info in headers
        headers.put(Exchange.HTTP_METHOD, request.getMethod());
        headers.put(Exchange.HTTP_QUERY, request.getQueryString());
        //headers.put(Exchange.HTTP_URL, request.getRequestURL());
        headers.put(Exchange.HTTP_URI, request.getRequestURI());
        headers.put(Exchange.HTTP_PATH, request.getPathInfo());
        headers.put(Exchange.CONTENT_TYPE, request.getContentType());
        headers.put(Exchange.HTTP_CHARACTER_ENCODING, request.getCharacterEncoding());

        return headers;
    }

    private Workflow findWorkflowContaining(String camelContextName) {
        Workflow foundWorkflow = null;
        for (WorkflowService wf : workflowServices) {
            CamelContext possibleContext = wf.getWorkflow().getCamelContext();
            if (camelContextName.equals(possibleContext.getName())) {
                foundWorkflow = wf.getWorkflow();
                break;
            }
        }
        return foundWorkflow;
    }

}
