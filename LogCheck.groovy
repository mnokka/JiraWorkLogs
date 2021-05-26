//  originally from:
//  https://jirasupport.wordpress.com/2015/08/31/new-jql-function-custom-to-retrive-issues-with-inactive-user-in-assignee-hasinactiveassignee/
// 	Jira 8 changes:
// https://community.atlassian.com/t5/Adaptavist-questions/Custom-JQL-function-membersOfProjectRole/qaq-p/717026

// Installation: Copy into JIRA scripts directory /com/onresolve/jira/groovy/jql
//
//
// Use Admin panel: Script Runner / Script JQL Functions /  scan (in the middle of the text)
// this fucntion should be found and added to list , for each iteration, buggy code will be dropped
//
// JQL USAGE: issueFunction in LogCheck("Project=XXX")





package com.onresolve.jira.groovy.jql // THIS DEFINES THE DIRECTORY


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.operand.QueryLiteral
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.jql.query.RangeQueryFactory
import com.atlassian.jira.util.MessageSetImpl
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.query.operand.FunctionOperand
import com.atlassian.query.operator.Operator
import org.apache.log4j.Category
import com.atlassian.jira.util.MessageSet
import com.atlassian.crowd.embedded.api.User
import com.atlassian.query.clause.TerminalClause
import com.atlassian.jira.jql.query.QueryCreationContext

import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery

import org.apache.lucene.index.Term
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import com.onresolve.jira.groovy.jql.AbstractScriptedJqlFunction
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.security.JiraAuthenticationContext;

import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.CustomFieldManager



class LogCheck extends AbstractScriptedJqlFunction implements JqlQueryFunction{
	@Override
	String getDescription() {
	"Function to find out issues with >999 work log entries"

	}

@Override
	List<Map> getArguments() {
[
[
		"description": "Subquery",
		"optional": false,
	]
]
}

@Override
String getFunctionName() {
"LogCheck"
}

def String subquery
//@Override
MessageSet validate(User user, FunctionOperand operand, TerminalClause terminalClause) {
def messageSet = new MessageSetImpl()
return messageSet
}

@Override
Query getQuery(QueryCreationContext queryCreationContext, FunctionOperand operand, TerminalClause terminalClause) {
	def mylogger = Logger.getLogger("LogCheck") // change for customer system
	mylogger.setLevel(Level.DEBUG) // was DEBUG
	CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
	mylogger.info("LogCheck JQL extension started")
	

	JiraAuthenticationContext context = ComponentAccessor.getJiraAuthenticationContext();
	ApplicationUser applicationUser = context.getUser();
	mylogger.info("Lxxxxxxxxxxxxxxxxxxx")
	mylogger.debug("goint ot ask issues")
	issues = getIssues(operand.args[0], applicationUser)
	//mylogger.debug("issues: $issues")
	
	def worklogManager = ComponentAccessor.worklogManager
	def issueManager = ComponentAccessor.issueManager
	
	BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
	
	log.debug("query doine")
	issues.each {Issue issue ->
	
	try{
			
	
			log.debug("Issue: ${issue}")


			def alllogs=worklogManager.getByIssue(issue)
			def numberoflogs=alllogs.size()
			
			mylogger.debug("alllogs: $alllogs")
			mylogger.debug("numberoflogs: $numberoflogs")
			
			
			if (numberoflogs > 5) {
				 log.debug("HIT. Adding to query results")
				 booleanQuery.add(new TermQuery(new Term("issue_id", issue.id as String)), BooleanClause.Occur.SHOULD)
				}
			
			else {
				log.debug("LOW value, not used")
				
			}
	
				   
		
	}catch(NullPointerException NPE){

	}
}

log.info("LogCheck JQL extension finished")
return booleanQuery.build();
}






}
