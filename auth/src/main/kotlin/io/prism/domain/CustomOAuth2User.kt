package io.prism.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import java.io.Serializable

class CustomOAuth2User(
    private val authorities: Set<GrantedAuthority>,
    private val attributes: Map<String, Any>,
    val nameAttributeKey: String,
    val email: String,
    private val name: String,
) : OAuth2User, Serializable {


    override fun getName(): String {
        return this.name
    }


    override fun getAttributes(): Map<String, Any> {
        return this.attributes
    }

    override fun getAuthorities(): Collection<out GrantedAuthority> {
        return this.authorities
    }
}
