package io.prism.adapter.security

enum class CustomHeaders(
    val key: String
) {
    AUTHORIZATION_SUPPORT("Authorization-Support");

    override fun toString(): String {
        return key
    }
}
