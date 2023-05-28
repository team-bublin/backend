package io.prism

import jakarta.persistence.Entity
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.tool.hbm2ddl.SchemaExport
import org.hibernate.tool.schema.TargetType
import org.reflections.Reflections
import java.util.*


private const val SCHEMA_GENERATE_TARGET_PACKAGE = "io.prism.entity"

private const val SCHEMA_DELIMITER = ";"

object GenerateSchemaSQL {
    @JvmStatic
    fun main(args: Array<String>) {
        val outputFilePath = args[0]

        val settings: Map<String, String> =
            mapOf(
                "hibernate.dialect" to "org.hibernate.dialect.MySQLDialect"
            )


        val metadata = MetadataSources(
            StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build()
        )

        val entityClasses = Reflections(SCHEMA_GENERATE_TARGET_PACKAGE)
            .getTypesAnnotatedWith(Entity::class.java)

        for (clazz in entityClasses) {
            metadata.addAnnotatedClass(clazz)
        }

        SchemaExport()
            .setDelimiter(SCHEMA_DELIMITER)
            .setOutputFile(outputFilePath)
            .setFormat(true)
            .createOnly(
                EnumSet.of(TargetType.SCRIPT),
                metadata.buildMetadata()
            )
    }
}
