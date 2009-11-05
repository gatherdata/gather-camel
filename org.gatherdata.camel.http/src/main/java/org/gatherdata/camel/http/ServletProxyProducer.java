package org.gatherdata.camel.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletProxyProducer {

    public void sendMessage(HttpServletRequest request, HttpServletResponse response, String toCamelContextName);

}