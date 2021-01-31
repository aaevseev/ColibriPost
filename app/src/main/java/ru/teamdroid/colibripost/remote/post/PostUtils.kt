package ru.teamdroid.colibripost.remote.post

import android.content.Context
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.domain.post.PostEntity

object PostUtils {

    fun isSameAlbum(albumId: Long, compareAlbumId:Long) = albumId == compareAlbumId

    fun filterMediaAlbumPosts(posts:List<PostEntity>): List<PostEntity>{
        val mediaAlbumIds = posts.filter { it.mediaAlbumId != 0L }.map { it.mediaAlbumId }
        val filteredAlbumIds = mutableListOf<Long>()
        var compareAlbumId = 0L

        mediaAlbumIds.forEach {
            if(isSameAlbum(it, compareAlbumId)) print("isSame")
            else {
                compareAlbumId = it
                filteredAlbumIds.add(it)
            }
        }
        val filteredAlbumPosts = mutableListOf<PostEntity>()
        val filteredPosts = mutableListOf<PostEntity>()

        filteredPosts.addAll(posts.filter { it.mediaAlbumId == 0L })
        filteredAlbumIds.forEach { id ->
            filteredAlbumPosts.add(posts.last { it.mediaAlbumId == id })
        }

        filteredPosts.addAll(filteredAlbumPosts)

        filteredPosts.sortBy { it.scheduleDate }

        return filteredPosts.reversed()

    }

    fun setImageText(posts:List<PostEntity>, context:Context){
        posts.map {
            post -> if(post.text == ""){
                if(post.mediaAlbumId == 0L) post.text = context.resources.getString(R.string.images)
                else post.text = context.resources.getString(R.string.image)
            }
        }
    }

}