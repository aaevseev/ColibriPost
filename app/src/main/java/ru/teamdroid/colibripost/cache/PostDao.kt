package ru.teamdroid.colibripost.cache

import androidx.room.*
import ru.teamdroid.colibripost.data.post.PostCache
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.post.PostEntity
import java.time.Month

@Dao
interface PostDao: PostCache {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(postEntity: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entities: List<PostEntity>): List<Long>

    @Update
    fun update(postEntity: PostEntity)

    @Transaction
    override fun savePost(entity: PostEntity) {
        insert(entity)
    }

    @Transaction
    override fun savePosts(entities: List<PostEntity>) {
        insert(entities)
    }

    @Query("SELECT EXISTS(SELECT * FROM posts_table WHERE Day = :day AND Month = :month AND Year = :year)")
    override fun dayHavePosts(day:Int, month:Int, year:Int):Int

    @Query("SELECT * FROM posts_table WHERE Day = :day AND Month = :month AND Year = :year")
    override fun getPostsByDay(day: Int, month: Int, year: Int): List<PostEntity>
}