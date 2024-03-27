package com.example.mostdelicious.models

import androidx.room.Entity
import com.example.mostdelicious.helpers.EMPTY_VALUE
import kotlinx.serialization.Serializable

@Entity(tableName = "other_users")
class OtherUser(
    id: String = EMPTY_VALUE,
    email: String = EMPTY_VALUE,
    name: String = EMPTY_VALUE,
) : User(id, email, name)
