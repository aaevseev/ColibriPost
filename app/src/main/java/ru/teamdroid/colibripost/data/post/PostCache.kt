package ru.teamdroid.colibripost.data.post

import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.ui.main.calendar.Post

interface PostCache {

    fun dayHavePosts(day:Int, month:Int, year:Int):Int
    fun getPostsByDay(day:Int, month:Int, year:Int):List<PostEntity>

    fun savePost(entity: PostEntity)

    fun savePosts(entities: List<PostEntity>)

    fun getRelatedPosts(messageId:Long, albumId:Long):List<PostEntity>

}