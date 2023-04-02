package io.prism.port.service

import io.prism.domain.entity.Member
import io.prism.domain.entity.Role

data class OAuthAttributes(
    val attributes: Map<String, Any>,
    val nameAttributeKey: String,
    val name: String,
    val email: String,
) {

    fun toEntity(): Member {
        return Member.new(
            email = email,
            name = name,
            role = Role.ROLE_USER
        )
    }

    companion object {
        fun of(registrationId: String, userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return when (registrationId) {
                "kakao" -> ofKaKao(userNameAttributeName, attributes)
                else -> throw IllegalArgumentException("Invalid registration ID")
            }
        }

        private fun ofKaKao(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            val kakaoAccount = attributes["kakao_account"] as Map<String, Any>
            val kakaoProfile = kakaoAccount!!["profile"] as Map<String, Any>

            return OAuthAttributes(
                name = kakaoProfile["nickname"].toString(),
                email = kakaoAccount["email"].toString(),
                attributes = attributes,
                nameAttributeKey = userNameAttributeName
            )
        }


    }
}
