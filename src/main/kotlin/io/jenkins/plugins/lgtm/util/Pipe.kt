package io.jenkins.plugins.lgtm.util

infix fun <T, R> T.`|`(block: (T) -> R): R = this.let(block)
