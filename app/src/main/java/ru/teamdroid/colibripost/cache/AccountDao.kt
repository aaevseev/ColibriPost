package ru.teamdroid.colibripost.cache

import androidx.room.*
import ru.teamdroid.colibripost.data.AccountCache
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.user.AccountEntity

@Dao
interface AccountDao : AccountCache{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friendEntity: AccountEntity): Long

    @Transaction
    override fun saveAccount(entity: AccountEntity) {
        insert(entity)
    }

    @Query("SELECT * from accounts_table")
    override fun getAccount(): AccountEntity?
}