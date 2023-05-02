package io.prism.converter

import io.prism.entity.collection.Selections
import org.jooq.Converter

class JooqSelectionsConverter : Converter<Selections, String> {
    override fun from(selections: Selections?): String {
        return selections?.toList()?.joinToString(SELECTIONS_DELIMITER) ?: ""
    }

    override fun to(string: String?): Selections {
        return if(string == null) Selections.new() else Selections.new(string)
    }

    override fun fromType(): Class<Selections> {
        return Selections::class.java
    }

    override fun toType(): Class<String> {
        return String::class.java
    }

    companion object {
        private const val SELECTIONS_DELIMITER : String = "#"
    }
}