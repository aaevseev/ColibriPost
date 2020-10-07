package ru.teamdroid.colibripost.domain.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.drinkless.td.libcore.telegram.TdApi

@Entity(tableName = "accounts_table")
data class AccountEntity(
    @PrimaryKey
    var id:Int = 0,
    @ColumnInfo(name ="first_name")
    var firstName: String = "",
    @ColumnInfo(name ="last_name")
    var lastName: String = "",
    @ColumnInfo(name ="photo_path")
    var photoPath: String = ""
){
    fun fill(user: TdApi.User){
        id = user.id
        firstName = user.firstName
        lastName = user.lastName
        user.profilePhoto?.let {
            photoPath = it.big.local.path
        }
    }
}