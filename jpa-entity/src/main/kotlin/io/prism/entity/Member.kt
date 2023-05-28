package io.prism.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "Member")
class Member protected constructor(
    id: Long?,
    username: String?,
    name: String?,
    email: String,
    role: Role,
    createdAt: LocalDateTime,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = id
        protected set

    @Column(name = "username")
    var username: String? = username

    @Column(name = "name")
    var name: String? = name
        protected set

    @Column(name = "email")
    var email: String = email
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    var role: Role = role
        protected set

    @Column(name = "created_at")
    var createdAt: LocalDateTime = createdAt
        protected set

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
