package ru.teamdroid.colibripost.remote.post

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.remote.Messages
import ru.teamdroid.colibripost.remote.core.TelegramClient
import ru.teamdroid.colibripost.ui.newpost.NewPostViewModel
import ru.teamdroid.colibripost.utils.FileUtils
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRequests @Inject constructor(
        private val client:TelegramClient,
        private val messages: Messages,
        private val context:Context
){

    suspend fun getSchedulePosts(chatIds: List<Long>, calendarDay:Long, day:Int, month:Int, year:Int, channelsIds:List<Long>): List<PostEntity>{

        val posts = mutableListOf<PostEntity>()

        chatIds.map { id ->
            getChatScheduleMessages(id).messages
        }.map {
            it.forEach {
                val date = Date(convertSendDateToLong(it))
                if(scheduleDateCompare(date, Date(calendarDay))){
                    if(channelsIds.size != 0){
                        if(isPostFromChannel(channelsIds, it)){
                            val post = PostEntity()
                            if (it.content is TdApi.MessagePhoto) getPostPhoto(it)
                            post.fill(it)
                            posts.add(post)
                        }
                    }else{
                        val post = PostEntity()
                        if (it.content is TdApi.MessagePhoto) getPostPhoto(it)
                        post.fill(it)
                        posts.add(post)
                    }
                }
            }
        }
        return posts
    }

    suspend fun deleteSchedulePost(chatId:Long, messagesIds:LongArray){
        return client.send(TdApi.DeleteMessages(chatId, messagesIds, true))
    }

    suspend fun getWeekScheduleInfo(chatIds: List<Long>, calendarDays:List<Long>): List<Boolean>{

        val posts = mutableListOf<PostEntity>()

        chatIds.map { id ->
            getChatScheduleMessages(id).messages
        }.map {
            val messages = mutableListOf<PostEntity>()
            it.forEach {
                val date = Date(convertSendDateToLong(it))
                if(scheduleWeekDateCompare(date, calendarDays)){
                    val post = PostEntity()
                    post.fill(it)
                    posts.add(post)
                }
            }
            posts.addAll(messages)
        }
        return getExistingInfo(calendarDays, PostUtils.filterMediaAlbumPosts(posts))
    }

    suspend fun getChatScheduleMessages(id:Long):TdApi.Messages{
        return client.send(TdApi.GetChatScheduledMessages(id))
    }

    //region Post Utils

    fun isPostFromChannel(channelsIds:List<Long>, message:TdApi.Message):Boolean{
        val indicate = channelsIds.firstOrNull { it == message.chatId }
        return indicate != null
    }

    fun scheduleWeekDateCompare(date: Date, selectedWeek:List<Long>): Boolean{

        val datesOfWeek = selectedWeek.map { Date(it) }

        val day = getDay(date)
        val month = getMonth(date)
        val year = getYear(date)

        var selectedDay = ""
        var selectedMonth = ""
        var selectedYear = ""

        var compareIndicate = false

        for(scheduleDay: Date in datesOfWeek){
            selectedDay = getDay(scheduleDay)
            selectedMonth = getMonth(scheduleDay)
            selectedYear = getYear(scheduleDay)

            if(day.equals(selectedDay) && month.equals(selectedMonth) && year.equals(selectedYear)) {
                compareIndicate = true
                break
            }
        }
        return compareIndicate
    }

    fun scheduleDateCompare(date: Date, selectedDate: Date): Boolean{
        val day = getDay(date)
        val month = getMonth(date)
        val year = getYear(date)
        val selectedDay = getDay(selectedDate)
        val selectedMonth = getMonth(selectedDate)
        val selectedYear = getYear(selectedDate)
        return day.equals(selectedDay) && month.equals(selectedMonth) && year.equals(selectedYear)
    }

    fun getExistingInfo(calendarDays: List<Long>, posts:List<PostEntity>):List<Boolean>{
        val existList = mutableListOf<Boolean>()
        for(i:Int in 0..6){
            if(posts.firstOrNull {
                        scheduleDateCompare(Date(it.scheduleDate * 1000L), Date(calendarDays.get(i)))
            } != null) existList.add(true)
            else existList.add(false)
        }
        return existList
    }

    fun getDay(scheduleDay: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return calendar.get(Calendar.DAY_OF_MONTH).toString()
    }

    fun getMonth(scheduleDay: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return (calendar.get(Calendar.MONTH)+1).toString()
    }

    fun getYear(scheduleDay: Date):String{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return calendar.get(Calendar.YEAR).toString()
    }

    fun convertSendDateToLong(message: TdApi.Message):Long{
        return (message.schedulingState as TdApi.MessageSchedulingStateSendAtDate).sendDate * 1000L
    }

    suspend fun getPostPhoto(message: TdApi.Message){
        (message.content as TdApi.MessagePhoto).photo.sizes[0].photo?.let {
            it.local = downloadFiles(it).local
            Log.d("CheckFile", it.local.path)
        }
    }

    suspend fun downloadFiles(file: TdApi.File): TdApi.File {
        return client.send(
                TdApi.DownloadFile(
                        file.id,
                        1,
                        file.local.downloadOffset,
                        file.local.downloadedPrefixSize,
                        true
                )
        )
    }

    fun getPhotoContent(content:TdApi.MessageContent): TdApi.File{
        return when(content){
            is TdApi.MessagePhoto -> content.photo.sizes[0].photo
            else -> TdApi.File()
        }

    }

    //endregion

    //region PostUtils

    suspend fun duplicatePost(chatId : Long, posts: List<PostEntity>) {
        when (posts.size) {
            0, 1 -> sendSimplePost(chatId, posts)
            else -> sendAlbum(chatId, posts)
        }
    }

    private suspend fun sendSimplePost(chatId : Long, posts: List<PostEntity>) {
        val content = combineContent(posts)
        val epoch = posts[0].scheduleDate
        Log.d("wow", "simple_Post")
        messages.sendMessage(
                chatId,
                content,
                epoch
        ).also { Log.d("NewPostViewModel", "sendPost: $it") }

    }

    private suspend fun sendAlbum(chatId: Long, posts: List<PostEntity>) {
        val content = createAlbum(posts)
        val epoch = posts[0].scheduleDate
        messages.sendAlbum(
                    chatId,
                    content,
                    epoch
        ).also { Log.d("NewPostViewModel", "sendPost: $it") }
    }


    private suspend fun createAlbum(posts: List<PostEntity>): Array<TdApi.InputMessageContent> {
        val list = mutableListOf<TdApi.InputMessageContent>()
        val uriList = posts.map { java.io.File(it.photoPath).toUri() }
        for ((index, uri) in uriList.withIndex()) {
            val file = TdApi.InputFileLocal(uri.recievePath() ?: break)
            val photo =
                    if (index == 0) {
                        TdApi.InputMessagePhoto().apply { caption = getFormattedTextContent(posts[0].text) }
                    } else {
                        TdApi.InputMessagePhoto()
                    }
            photo.photo = file
            list += photo
        }

        return list.toTypedArray().also { Log.d("NewPostViewModel", "createAlbum: $it") }
    }


    private suspend fun combineContent(posts: List<PostEntity>): TdApi.InputMessageContent {
        val txt = getFormattedTextContent(posts[0].text)
        val list = posts.map { java.io.File(it.photoPath).toUri() }
        return when (list.size) {
            0 -> {
                TdApi.InputMessageText(txt, false, false)
            }
            1 -> {
                Log.d("wow111", FileUtils.getRealPath(context, list[0]))
                val file = TdApi.InputFileLocal(FileUtils.getRealPath(context, list[0]))
                val photo = TdApi.InputMessagePhoto()
                photo.photo = file
                photo.caption = txt
                photo
            }
            else -> throw IllegalStateException("files more than 1")
        }
    }

    private suspend fun getFormattedTextContent(postText:String): TdApi.FormattedText{
            return TdApi.FormattedText(postText, null)
    }

    private fun Uri.recievePath(): String? {
        val cursor: Cursor =
                context.getContentResolver().query(this, null, null, null, null) ?: return null
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    companion object {
        private const val dayFormat = "dd.MM.yyyy"
        private const val timeFormat = "HH:mm"
    }

    //endregion
}
