package io.prism.entity.mapping

import io.prism.entity.Member
import io.prism.entity.Topic
import jakarta.persistence.*

@Entity
class MemberTopic protected constructor(
    id: Long?,
    member: Member?,
    topic: Topic?,
    answer: Int?,
    answerSection: Double?
) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = id
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val member: Member? = member

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topicId", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val topic: Topic? = topic

    var answer: Int? = answer
    var answerSection: Double? = answerSection

    fun updateAnswer(newAnswer : Int) {
        this.answer = newAnswer
    }

    fun updateAnswerSection(newAnswerSection : Double) {
        this.answerSection = newAnswerSection
    }

    companion object {
        fun create(
            member: Member,
            topic: Topic,
            answer: Int,
            answerSection: Double
        ): MemberTopic {
           return  MemberTopic(null, member, topic, answer, answerSection)
        }
    }
}