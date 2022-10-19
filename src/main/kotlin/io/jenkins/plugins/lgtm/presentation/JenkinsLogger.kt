package io.jenkins.plugins.lgtm.presentation

object JenkinsLogger {
    var delegate = System.out!!

    fun info(message: String) {
        delegate.println(message)
    }
}
