package io.prism.collection

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.prism.entity.collection.Selections

class SelectionsTest : ShouldSpec({
    should("입력 받은 문자열을 토대로 Contents를 생성한다.") {
        val selections: Selections = Selections.new("내용1", "내용2")

        selections.size() shouldBe 2
        selections.toList() shouldBe listOf("내용1", "내용2")
    }

    should("빈 문자열이 포함되어 전달될 경우 해당 내용은 제외하여 생성한다.") {
        val selections: Selections = Selections.new("", "내용2")

        selections.size() shouldBe 1
        selections.toList() shouldBe listOf("내용2")
    }

    should("구분자 #을 이용하여 하나의 문자열을 넘겨도 구분자를 통해 분리되어 생성한다.") {
        val selections: Selections = Selections.new("내용1#내용2")

        selections.size() shouldBe 2
        selections.toList() shouldBe listOf("내용1", "내용2")
    }
})
