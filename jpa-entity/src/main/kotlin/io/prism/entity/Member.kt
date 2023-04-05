package io.prism.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class Member protected constructor(
    id: Long?,
    username: String?,
    name: String?,
    email: String,
    role: Role,
    createdAt: LocalDateTime,
) {
    @Id
    var id: Long? = id
        protected set

    val username: String? = username

    var name: String? = name
        protected set

    var email: String = email
        protected set

    @Enumerated(EnumType.STRING)
    var role: Role = role
        protected set

    val createdAt: LocalDateTime = createdAt

    fun update(name: String) {
        this.name = name
    }

    companion object {
        fun new(
            username: String? = null,
            email: String,
            name: String,
            role: Role
        ): Member {
            return Member(null, username, name, email, role, LocalDateTime.now())
        }
    }
}
