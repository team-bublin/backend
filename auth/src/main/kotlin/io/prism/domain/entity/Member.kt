package io.prism.domain.entity

import java.time.LocalDateTime

class Member protected constructor(
    id: Long?,
    username: String?,
    name: String?,
    email: String,
    role: Role,
    createdAt: LocalDateTime,
) {
    var id: Long? = id
        protected set
    val username: String? = username

    var name: String? = name
        protected set
    var email: String = email
        protected set
    var role: Role = role
        protected set

    val createdAt: LocalDateTime = createdAt

    fun update(name: String) {
        this.name = name
    }

    companion object {
        fun new(
            username: String? = null,
            password: String? = null,
            email: String,
            name: String,
            role: Role
        ): Member {
            return Member(null, username, name, email, role, LocalDateTime.now())
        }
    }
}
