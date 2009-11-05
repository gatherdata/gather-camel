/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.service.archive.internal;


import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.apache.camel.Message;
import org.gatherdata.archiver.core.model.GatherArchive;
import org.gatherdata.camel.core.GatherHeaders;
import org.hamcrest.core.IsNot;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

public class ArchiveServiceWrapperImplTest {
    
    @Before
    public void prepareForTests() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.ALL);
    }

    @Test
    public void shouldTransformMessageBodyAsString() {
        ArchiveServiceWrapperImpl serviceWrapper = new ArchiveServiceWrapperImpl();
        
        Message messageToTransform =  (Message)createMock(Message.class);
        String mockBody = "this is only a test";
        Map<String, Object> mockHeaders = new HashMap<String,Object>();
        
        expect(messageToTransform.getBody(String.class)).andReturn(mockBody);
        expect(messageToTransform.getHeader(GatherHeaders.CBID_HEADER)).andReturn(null);
        expect(messageToTransform.getHeaders()).andReturn(mockHeaders);
        
        replay(messageToTransform);
        
        GatherArchive transformedArchive = serviceWrapper.transform(messageToTransform);
        
        assertThat(transformedArchive.getContent(), is(equalTo((Serializable)mockBody)));
        
        verify(messageToTransform);

    }
    
    /**
     * The transform should generate a uid for the message when the message header
     * doesn't specify one. 
     */
    @Test
    public void shouldGenerateUidWhenHeaderIsMissing() {
        ArchiveServiceWrapperImpl serviceWrapper = new ArchiveServiceWrapperImpl();
        
        Message messageToTransform =  (Message)createMock(Message.class);
        String mockBody = "this is only a test";
        Map<String, Object> mockHeaders = new HashMap<String,Object>();
        
        expect(messageToTransform.getBody(String.class)).andReturn(mockBody);
        expect(messageToTransform.getHeader(GatherHeaders.CBID_HEADER)).andReturn(null);
        expect(messageToTransform.getHeaders()).andReturn(mockHeaders);

        replay(messageToTransform);
        
        GatherArchive transformedArchive = serviceWrapper.transform(messageToTransform);
        
        assertThat(transformedArchive.getUid(), notNullValue());
        
        verify(messageToTransform);

    }

    @Test
    public void shouldUseCbidAsHeaderIfAvailable() throws URISyntaxException {
        ArchiveServiceWrapperImpl serviceWrapper = new ArchiveServiceWrapperImpl();
        
        Message messageToTransform =  (Message)createMock(Message.class);
        String mockBody = "this is only a test";
        Map<String, Object> mockHeaders = new HashMap<String,Object>();
        URI mockCbidHeader = new URI("mock:1234");
        
        expect(messageToTransform.getBody(String.class)).andReturn(mockBody);
        expect(messageToTransform.getHeader(GatherHeaders.CBID_HEADER)).andReturn(mockCbidHeader);
        expect(messageToTransform.getHeaders()).andReturn(mockHeaders);

        replay(messageToTransform);
        
        GatherArchive transformedArchive = serviceWrapper.transform(messageToTransform);
        
        assertThat(transformedArchive.getUid(), is(mockCbidHeader));
        
        verify(messageToTransform);

    }
    @Test
    public void shouldSetDateTimeToNowOnTransformedMessage() {
        ArchiveServiceWrapperImpl serviceWrapper = new ArchiveServiceWrapperImpl();
        
        Message messageToTransform =  (Message)createMock(Message.class);
        String mockBody = "this is only a test";
        Map<String, Object> mockHeaders = new HashMap<String,Object>();
        
        expect(messageToTransform.getBody(String.class)).andReturn(mockBody);
        expect(messageToTransform.getHeader(GatherHeaders.CBID_HEADER)).andReturn(null);
        expect(messageToTransform.getHeaders()).andReturn(mockHeaders);

        replay(messageToTransform);
        
        DateTime aboutNow = new DateTime();
        GatherArchive transformedArchive = serviceWrapper.transform(messageToTransform);
        
        // ABKTODO: revise these tests to avoid the possible false-negative when comparing
        // slightly different times which cross any of these boundaries
        assertThat(transformedArchive.getDateCreated().getYear(), is(aboutNow.getYear()));
        assertThat(transformedArchive.getDateCreated().getDayOfYear(), is(aboutNow.getDayOfYear()));
        assertThat(transformedArchive.getDateCreated().getHourOfDay(), is(aboutNow.getHourOfDay()));
        assertThat(transformedArchive.getDateCreated().getMinuteOfHour(), is(aboutNow.getMinuteOfHour()));
        
        
        verify(messageToTransform);

    }
    
    @Test
    public void shouldCopyAllMessageHeadersIntoMetadata() {
        ArchiveServiceWrapperImpl serviceWrapper = new ArchiveServiceWrapperImpl();
        
        Message messageToTransform =  (Message)createMock(Message.class);
        String mockBody = "this is only a test";
        Map<String, Object> mockHeaders = new HashMap<String, Object>();
        
        expect(messageToTransform.getBody(String.class)).andReturn(mockBody);
        expect(messageToTransform.getHeader(GatherHeaders.CBID_HEADER)).andReturn(null);
        expect(messageToTransform.getHeaders()).andReturn(mockHeaders);

        replay(messageToTransform);
        
        GatherArchive transformedArchive = serviceWrapper.transform(messageToTransform);
        
        assertThat(transformedArchive.getMetadata(), notNullValue());
    }

}
