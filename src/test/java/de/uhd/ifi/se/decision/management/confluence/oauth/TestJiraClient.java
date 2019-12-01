package de.uhd.ifi.se.decision.management.confluence.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.atlassian.sal.api.component.ComponentLocator;

import de.uhd.ifi.se.decision.management.confluence.mocks.MockComponentLocator;
import de.uhd.ifi.se.decision.management.confluence.oauth.impl.JiraClientImpl;

public class TestJiraClient {

	private static JiraClient jiraClient;

	@BeforeClass
	public static void setUp() {
		ComponentLocator.setComponentLocator(new MockComponentLocator());
		jiraClient = new JiraClientImpl();
	}

	@Test
	public void testConstructor() {
		assertNotNull(new JiraClientImpl());
	}

	@Test
	public void testGetCurrentActiveJiraProjects() {
		Set<String> projectKeys = jiraClient.getJiraProjects();
		assertEquals(1, projectKeys.size());
	}

	@Test
	public void testGetDecisionKnowledgeFromJira() {
		String decisionKnowledgeJsonString = jiraClient.getDecisionKnowledgeFromJira("", "CONDEC");
		assertEquals("[[{'type':'issue'}, {'type':'decision'}]]", decisionKnowledgeJsonString);
	}

	@Test
	public void testGetDecisionKnowledgeFromJiraByKeys() {
		Set<String> jiraIssueKeys = new HashSet<>();
		jiraIssueKeys.add("CONDEC-1");
		jiraIssueKeys.add("CONDEC-2");

		String decisionKnowledgeJsonString = jiraClient.getDecisionKnowledgeFromJira(jiraIssueKeys);
		assertEquals("[[{'type':'issue'}, {'type':'decision'}]]", decisionKnowledgeJsonString);
	}

	@Test
	public void testParseJiraProjectsJsonOneProject() {
		Set<String> projects = ((JiraClientImpl) jiraClient).parseJiraProjectsJson("CONDEC");
		assertEquals("CONDEC", projects.iterator().next());
	}

	@Test
	public void testParseJiraProjectsJsonManyProjects() {
		Set<String> projects = ((JiraClientImpl) jiraClient)
				.parseJiraProjectsJson("[ {'key' : 'TEST'}, {'key' : 'CONDEC'} ]");
		assertEquals(2, projects.size());
	}

	@Test
	public void testParseJiraIssueKeys() {
		Set<String> jiraIssueKeys = JiraClient
				.getJiraIssueKeys("ConDec-1: Initial commit ConDec-2 -hallo ConDec-3 -Great tool");
		Iterator<String> iterator = jiraIssueKeys.iterator();
		assertEquals("CONDEC-1", iterator.next());
		assertEquals("CONDEC-2", iterator.next());
		assertEquals("CONDEC-3", iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void testRetrieveProjectKeys() {
		Set<String> jiraIssueKeys = new HashSet<String>();
		assertEquals("", JiraClient.retrieveProjectKey(jiraIssueKeys));
		jiraIssueKeys.add("UNKNOWNPROJECT-1");
		assertEquals("", JiraClient.retrieveProjectKey(jiraIssueKeys));

		jiraIssueKeys.add("CONDEC-1");
		jiraIssueKeys.add("CONDEC-2");
		assertEquals("CONDEC", JiraClient.retrieveProjectKey(jiraIssueKeys));
	}

	@Test
	public void testGetJiraCallQuery() {
		Set<String> jiraIssueKeys = new HashSet<String>();
		jiraIssueKeys.add("CONDEC-1");
		jiraIssueKeys.add("CONDEC-2");
		assertEquals("%3Fjql%3Dkey+in+%28CONDEC-2%2CCONDEC-1%29", JiraClient.getJiraCallQuery(jiraIssueKeys));
	}

}