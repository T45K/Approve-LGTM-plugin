package io.jenkins.plugins.lgtm.dataclass

data class JsonStructure(
    val intValue: Int,
    val stringValue: String,
    val booleanValue: Boolean,
    val nullValue: Any?,
    val objectValue: Inner,
    val listValue: List<String>
) {
    data class Inner(val key: String)
}
