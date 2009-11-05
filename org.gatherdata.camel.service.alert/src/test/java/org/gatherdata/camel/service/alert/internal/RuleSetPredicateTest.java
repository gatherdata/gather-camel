/**
 * The contents of this file are subject to the AED Public Use License Agreement, Version 1.0 (the "License");
 * use in any manner is strictly prohibited except in compliance with the terms of the License.
 * The License is available at http://gatherdata.org/license.
 *
 * Copyright (c) AED.  All Rights Reserved
 */
package org.gatherdata.camel.service.alert.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;
import static org.gatherdata.alert.builder.LanguageScriptBuilder.expressedIn;
import static org.gatherdata.alert.builder.PlannedNotificationBuilder.address;
import static org.gatherdata.alert.builder.RuleSetBuilder.rules;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.EndpointInject;
import org.apache.camel.Predicate;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.gatherdata.alert.builder.ActionPlanBuilder;
import org.gatherdata.alert.core.model.ActionPlan;
import org.gatherdata.alert.core.model.LanguageScript;
import org.gatherdata.alert.core.model.RuleSet;
import org.gatherdata.alert.core.model.impl.MutableRuleSet;
import org.gatherdata.alert.core.spi.AlertService;
import org.gatherdata.camel.core.PredicateService;
import org.junit.Test;

/**
 * Behavior tests for the implementation of the RuleSetPredicate.
 * 
 * Uses lots of fun stuff: CamelTestSupport, EasyMock, and the "simple"
 * scripting language.
 * 
 * @see <a href="http://camel.apache.org/simple.html">Apache Simple</a>
 * 
 */
public class RuleSetPredicateTest extends CamelTestSupport {

	final AlertServicePredicate rulesetPredicate = new AlertServicePredicate();

	@EndpointInject(uri = "mock:result")
	protected MockEndpoint resultEndpoint;

	@Produce(uri = "direct:start")
	protected ProducerTemplate template;

	@Override
	protected RouteBuilder createRouteBuilder() {

		return new RouteBuilder() {
			public void configure() {
				from("direct:start").filter(rulesetPredicate).to("mock:result");
			}
		};
	}

	@Test
	public void shouldMatchUsingRulesFromAPlan() throws InterruptedException {
		String expectedBody = "<matched/>";


		ActionPlan testPlan = ActionPlanBuilder.plan()
			.named("testMatch")
				.describedAs("should match using rules from a plan")
				.applyingRules(
						rules(PredicateService.XML_CONTEXT)
						.rule(expressedIn("simple")
								.script("${in.header.foo} == bar")
						)
					)
				.build();
		List<RuleSet> testRules = new ArrayList<RuleSet>();
		testRules.add(testPlan.getRuleSet());

		AlertService mockAlertService = createMock(AlertService.class);
		expect(
				mockAlertService
						.getActiveRulesetsFor(PredicateService.XML_CONTEXT))
				.andReturn(testRules);
		
		rulesetPredicate.alertService = mockAlertService;

		replay(mockAlertService);

		template.sendBodyAndHeader(expectedBody, "foo", "bar");

		verify(mockAlertService);

		resultEndpoint.assertIsSatisfied();

	}

	@Test
	public void shouldNotMatchOnUnmatchedInput() throws Exception {
		resultEndpoint.expectedMessageCount(0);

		ActionPlan testPlan = ActionPlanBuilder.plan()
			.named("testMatch")
				.describedAs("should match using rules from a plan")
				.applyingRules(
						rules(PredicateService.XML_CONTEXT)
						.rule(expressedIn("simple")
								.script("${in.header.foo} == bar")
						)
					)
				.build();
		List<RuleSet> testRules = new ArrayList<RuleSet>();
		testRules.add(testPlan.getRuleSet());

		AlertService mockAlertService = createMock(AlertService.class);
		expect(
				mockAlertService
						.getActiveRulesetsFor(PredicateService.XML_CONTEXT))
				.andReturn(testRules);
		
		rulesetPredicate.alertService = mockAlertService;

		replay(mockAlertService);

		template.sendBodyAndHeader("<notMatched/>", "foo",
				"notMatchedHeaderValue");

		verify(mockAlertService);

		resultEndpoint.assertIsSatisfied();
	}

	@Test
	public void shouldMatchAllScriptsOnSatisfyAll() throws InterruptedException {
		String expectedBody = "<matched/>";

		ActionPlan testPlan = ActionPlanBuilder.plan()
		.named("testMatch")
			.describedAs("should match using rules from a plan")
			.applyingRules(
					rules(PredicateService.XML_CONTEXT)
					.rule(expressedIn("simple")
							.script("${in.header.foo} == bar")
							
					)
					.rule(expressedIn("simple")
							.script("${in.body} contains matched")
							
					)
					.satisfyAll(true)
				)
			.build();
		List<RuleSet> testRules = new ArrayList<RuleSet>();
		testRules.add(testPlan.getRuleSet());

		AlertService mockAlertService = createMock(AlertService.class);
		expect(mockAlertService.getActiveRulesetsFor(PredicateService.XML_CONTEXT))
			.andReturn(testRules);
		
		rulesetPredicate.alertService = mockAlertService;

		replay(mockAlertService);

		template.sendBodyAndHeader(expectedBody, "foo", "bar");

		verify(mockAlertService);

		resultEndpoint.assertIsSatisfied();

	}

	@Test
	public void shouldNotMatchOnSatisfyAllIfOneScriptIsFalse()
			throws InterruptedException {
		String messageBody = "<unexpected/>";
		resultEndpoint.expectedMessageCount(0);

		ActionPlan testPlan = ActionPlanBuilder.plan()
		.named("testMatch")
			.describedAs("should match using rules from a plan")
			.applyingRules(
					rules(PredicateService.XML_CONTEXT)
					.rule(expressedIn("simple")
							.script("${in.header.foo} == bar")
							
					)
					.rule(expressedIn("simple")
							.script("${in.body} contains matched")
							
					)
					.satisfyAll(true)
				)
			.build();
		List<RuleSet> testRules = new ArrayList<RuleSet>();
		testRules.add(testPlan.getRuleSet());

		AlertService mockAlertService = createMock(AlertService.class);
		expect(mockAlertService.getActiveRulesetsFor(PredicateService.XML_CONTEXT))
			.andReturn(testRules);
		
		rulesetPredicate.alertService = mockAlertService;

		replay(mockAlertService);

		template.sendBodyAndHeader(messageBody, "foo", "bar");

		verify(mockAlertService);

		resultEndpoint.assertIsSatisfied();

	}

}
