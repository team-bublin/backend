package io.prism.entity

import io.prism.entity.collection.Selections
import io.prism.entity.collection.SelectionsConverter
import jakarta.persistence.*

@Entity
class Topic protected constructor(
    id: Long?,
    title: String?,
    selections: Selections?,
    totalAverage: Double?
){
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = id
        protected set

    @Column
    var title: String? = title
        protected set

    @Column
    @Embedded
    @Convert(converter = SelectionsConverter::class, attributeName = "selections")
    var selections: Selections? = selections
        protected set

    //TODO : MemberTopic에 들어가있는 answerSection값을 토대로 계산하여 업데이트하는 로직 추가 예정
    @Column
    var totalAverage: Double? = totalAverage
        protected set

    companion object {
        fun new(
            title: String,
            selections: Selections,
            totalAverage: Double
        ): Topic {
            return Topic(null, title, selections, totalAverage)
        }
    }
}

