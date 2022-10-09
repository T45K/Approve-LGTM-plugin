package io.jenkins.plugins.lgtm

import hudson.model.Label
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class ApproveLgtmBuilderTest extends Specification {

    @Rule
    JenkinsRule jenkins = new JenkinsRule()

    final def name = "Bobby"

    def 'test config roundtrip'() {
        given:
        def project = jenkins.createFreeStyleProject()
        project.buildersList << new ApproveLgtmBuilder(name)

        when:
        project = jenkins.configRoundtrip(project)

        then:
        jenkins.assertEqualDataBoundBeans(new ApproveLgtmBuilder(name), project.buildersList[0])
    }

    def 'test config roundtrip French'() {
        def project = jenkins.createFreeStyleProject()
        final def builder = new ApproveLgtmBuilder(name)
        builder.useFrench = true
        project.buildersList << builder
        project = jenkins.configRoundtrip(project)

        when:
        final def lhs = new ApproveLgtmBuilder(name)
        lhs.useFrench = true

        then:
        jenkins.assertEqualDataBoundBeans(lhs, project.buildersList[0])
    }

    def 'test build'() {
        given:
        final def project = jenkins.createFreeStyleProject()
        final def builder = new ApproveLgtmBuilder(name)

        project.buildersList << builder

        when:
        final def build = jenkins.buildAndAssertSuccess(project)

        then:
        jenkins.assertLogContains("Hello, " + name, build)
    }

    def 'test build French'() {
        given:
        final def project = jenkins.createFreeStyleProject()
        final def builder = new ApproveLgtmBuilder(name)
        builder.useFrench = true
        project.buildersList << builder

        when:
        final def build = jenkins.buildAndAssertSuccess(project)

        then:
        jenkins.assertLogContains("Bonjour, " + name, build)
    }

    def 'test scripted pipeline'() {
        final def agentLabel = "my-agent"
        jenkins.createOnlineSlave(Label.get(agentLabel))
        final def job = jenkins.createProject(WorkflowJob.class, "test-scripted-pipeline")
        final def pipelineScript = """
node {
  greet '$name'
}"""
        job.definition = new CpsFlowDefinition(pipelineScript, true)

        when:
        final def completedBuild = jenkins.assertBuildStatusSuccess(job.scheduleBuild2(0))

        then:
        final def expectedString = "Hello, $name!"
        jenkins.assertLogContains(expectedString, completedBuild)
    }
}