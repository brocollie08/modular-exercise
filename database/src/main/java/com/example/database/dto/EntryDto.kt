package com.example.database.dto

import androidx.room.*
import java.lang.reflect.Type

enum class EntryType {
    CASH_INVESTMENT,
    LONG_TERM_ASSET,
    SHORT_TERM_LIABILITY,
    LONG_TERM_DEBT
}


class ProductTypeConverters {

    @TypeConverter
    fun toType(value: String) = enumValueOf<EntryType>(value)

    @TypeConverter
    fun fromType(value: EntryType) = value.name
}

sealed class MySealedClass(var classTitle: String) {

    data class Header(val title: String): MySealedClass(title)

    @Entity(tableName = "entries", indices = [Index(value = ["entryName", "owner"], unique = true)])
    @TypeConverters(ProductTypeConverters::class)
    data class EntryDto (
        var entryName: String = "",
        var value: Float?,
        var type: EntryType?,
        var owner: String = ""
    ): MySealedClass(entryName) {
        @PrimaryKey(autoGenerate = true)
        var entryId: Int = 0
    }

    data class Footer(
        val title: String,
        var value: Float?
    ): MySealedClass(title)

    data class Adder(
        val title: String,
        var type: EntryType
    ): MySealedClass(title)
}