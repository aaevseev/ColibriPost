package ru.teamdroid.colibripost.cache

import androidx.room.*
import ru.teamdroid.colibripost.data.ChannelsCache
import ru.teamdroid.colibripost.domain.channels.ChannelEntity

@Dao
interface ChannelsDao : ChannelsCache{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(friendEntity: ChannelEntity): Long

    @Update
    fun update(friendEntity: ChannelEntity)

    @Transaction
    override fun saveChannel(entity: ChannelEntity) {
        if(insert(entity) == -1L) update(entity)
    }

    @Query("SELECT * from channels_table WHERE chat_id = :key")
    override fun getChannel(key: Long): ChannelEntity?

    @Query("SELECT * from channels_table")
    override fun getChannels(): List<ChannelEntity>

    @Query("DELETE FROM channels_table WHERE chat_id = :key")
    override fun removeChannelEntity(key: Long)
}