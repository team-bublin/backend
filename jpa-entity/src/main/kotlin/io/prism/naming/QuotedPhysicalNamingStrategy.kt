package io.prism.naming

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment
import java.util.*

class QuotedPhysicalNamingStrategy : PhysicalNamingStrategyStandardImpl() {

    override fun toPhysicalCatalogName(name: Identifier?, context: JdbcEnvironment?): Identifier? {
        return  if (name == null) null
                else Identifier.quote(super.toPhysicalCatalogName(name, context))
    }

    override fun toPhysicalSchemaName(name: Identifier?, context: JdbcEnvironment?): Identifier? {
        return if (name == null) null
               else Identifier.quote(super.toPhysicalSchemaName(name, context))
    }

    override fun toPhysicalTableName(name: Identifier?, context: JdbcEnvironment?): Identifier? {
        return  if (name == null) null
                else Identifier.quote(super.toPhysicalTableName(name, context))
    }

    override fun toPhysicalSequenceName(name: Identifier?, context: JdbcEnvironment?): Identifier? {
        return  if (name == null) null
                else Identifier.quote(super.toPhysicalSequenceName(name, context))
    }

    override fun toPhysicalColumnName(name: Identifier?, context: JdbcEnvironment?): Identifier? {
        return  if (name == null) null
                else Identifier.quote(super.toPhysicalColumnName(name, context))
    }
}
