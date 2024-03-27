package com.example.mostdelicious.database.local

import androidx.room.TypeConverter
import com.example.mostdelicious.helpers.json
import kotlinx.serialization.encodeToString

class Converters {
    @TypeConverter
    fun fromStringToListOfStrings(string: String): List<String> {
        return json.decodeFromString<List<String>>(string)
    }

    @TypeConverter
    fun fromListOfStringsToString(strings: List<String>): String {
        return json.encodeToString(strings)
    }
}