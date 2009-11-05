/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.example.http.internal;

import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gatherdata.camel.http.ServletProxyProducer;

import com.google.inject.Inject;

public class PostMessageServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 657285692583339037L;

    private static final Logger log = Logger.getLogger(PostMessageServlet.class.getName());

    @Inject
    ServletProxyProducer camelProxy;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        log.info("doPost()");
        if (camelProxy != null) {
            camelProxy.sendMessage(request, response, "xforms-workflow");
        } else {
            log.warning("no ServletProxyProducer available for sending camel messages");
        }
    }
}
