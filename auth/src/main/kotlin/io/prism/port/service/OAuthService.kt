package io.prism.port.service

import io.prism.domain.CustomOAuth2User
import io.prism.domain.entity.Member
import io.prism.port.repository.MemberRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class OAuthService(private val memberRepository: MemberRepository) :
    ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest): Mono<OAuth2User> {
        val delegate = DefaultReactiveOAuth2UserService()
        return delegate.loadUser(userRequest)
            .flatMap {
                val registrationId = userRequest.clientRegistration.registrationId
                val userNameAttributeName =
                    userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
                val attributes =
                    OAuthAttributes.of(registrationId, userNameAttributeName, it.attributes)
                val member: Member = saveOrUpdate(attributes)
                Mono.just(
                    CustomOAuth2User(
                        setOf(SimpleGrantedAuthority(member.role.name)),
                        attributes.attributes,
                        attributes.nameAttributeKey,
                        attributes.email,
                        attributes.name
                    )
                )
            }
    }

    private fun saveOrUpdate(oAuthAttributes: OAuthAttributes): Member {
        return memberRepository.findByEmail(oAuthAttributes.email)?.let {
            it.update(oAuthAttributes.name)
            it
        } ?: memberRepository.save(oAuthAttributes.toEntity())
    }
}
