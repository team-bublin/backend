package io.prism.port.repository

import io.prism.domain.entity.Member
import io.prism.domain.tables.Member.MEMBER
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class MemberRepository(private val dsl: DSLContext) {

    fun save(member: Member): Member {
        dsl.insertInto(MEMBER)
            .set(MEMBER.USERNAME, member.username)
            .set(MEMBER.NAME, member.name)
            .set(MEMBER.EMAIL, member.email)
            .set(MEMBER.ROLE, member.role.name)
            .set(MEMBER.CREATED_AT, member.createdAt)
            .execute()
        return member
    }

    fun findById(id: Long): Member? {
        return dsl.select(MEMBER)
            .from(MEMBER)
            .where(MEMBER.ID.eq(id))
            .fetchOneInto(Member::class.java)
    }

    fun findByEmail(email: String): Member? {
        return dsl.select(MEMBER)
            .from(MEMBER)
            .where(MEMBER.EMAIL.eq(email))
            .fetchOneInto(Member::class.java)
    }
}
