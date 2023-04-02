package io.prism.repository

import io.prism.domain.entity.Member
import io.prism.domain.entity.Role
import io.prism.port.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MemberRepositoryTest @Autowired constructor(
    private val memberRepository: MemberRepository
) {

    @Test
    fun findById() {
        val save = memberRepository.save(Member.new("hi", "asdf", "test@gmail.com", role = Role.ROLE_USER, name= "hi"))
        val findById = memberRepository.findById(2L)
        println(findById)
        println("hi")
    }
}
