package io.prism.entity.collection

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Convert


@Convert
class SelectionsConverter : AttributeConverter<Selections, String> {
    override fun convertToDatabaseColumn(selections: Selections?): String {
        return selections?.toList()?.joinToString(SELECTIONS_DELIMITER) ?: ""
    }

    override fun convertToEntityAttribute(string: String?): Selections {
        return if(string == null) Selections.new() else Selections.new(string)
    }

    companion object {
        private const val SELECTIONS_DELIMITER : String = "#"
    }
}