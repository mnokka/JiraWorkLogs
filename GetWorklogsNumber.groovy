
// Try to find simple way to show number of Jira worklogs of an issue

import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.worklog.Worklog
import com.atlassian.jira.component.ComponentAccessor

def TESTISSUE="SUPPORT-173"

// set logging to Jira log
def Logger mylogger
mylogger = Logger.getLogger("GetWorkglogs")
mylogger.setLevel(Level.DEBUG ) // or INFO (prod) or DEBUG (development)


mylogger.debug("Started")

def worklogManager = ComponentAccessor.worklogManager
def issueManager = ComponentAccessor.issueManager

def issue = issueManager.getIssueObject(TESTISSUE)
def alllogs=worklogManager.getByIssue(issue)
def numberoflogs=alllogs.size()

mylogger.debug("alllogs: $alllogs")
mylogger.debug("numberoflogs: $numberoflogs")
