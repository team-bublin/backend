package io.prism.entity.collection

import jakarta.persistence.Embeddable
import org.springframework.util.StringUtils

@Embeddable
class Selections(
    private val selections: List<String> = listOf()
) {

    fun size(): Int {
        return selections.size
    }

    fun toList(): List<String> {
        return selections.stream()
            .toList()
    }

    fun toStringWithDelimiter(): String {
        return selections.joinToString(SELECTIONS_DELIMITER)
    }

    companion object {
        private const val SELECTIONS_DELIMITER: String = "#"
        private const val FIRST_ELEMENT: Int = 0

        fun new(vararg contents: String): Selections {

            val contentList = toList(contents)

            return Selections(filter(contentList))
        }

        private fun singleContentWithDelimiter(contents: String) =
            contents.contains(SELECTIONS_DELIMITER)

        private fun toList(contents: Array<out String>): List<String> {
            val firstElement = contents[FIRST_ELEMENT]

            return if (contents.size == 1 && singleContentWithDelimiter(firstElement)) {
                firstElement.split(SELECTIONS_DELIMITER)
            } else {
                contents.toList()
            }
        }

        private fun filter(
            contents: List<String>,
        ): MutableList<String> {
            return contents.stream()
                .filter { content ->
                    StringUtils.hasText(content)
                }
                .toList()
        }
    }
}