package io.jenkins.plugins.lgtm.util

@Suppress("FunctionName", "unused", "DANGEROUS_CHARACTERS")
infix fun <T, R> T.`|`(block: (T) -> R): R = this.let(block)
