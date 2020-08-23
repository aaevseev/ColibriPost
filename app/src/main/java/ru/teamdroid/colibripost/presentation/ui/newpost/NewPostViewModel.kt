package ru.teamdroid.colibripost.presentation.ui.newpost

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi.*
import ru.teamdroid.colibripost.data.Chats
import ru.teamdroid.colibripost.data.Messages
import ru.teamdroid.colibripost.data.TelegramClient
import ru.teamdroid.colibripost.other.SingleLiveEvent
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject


class NewPostViewModel @Inject constructor(
    val client: TelegramClient,
    val chats: Chats,
    val messages: Messages,
    val context: Context
) : ViewModel() {
    companion object {
        private const val dayFormat = "dd.MM.yyyy"
        private const val timeFormat = "HH:mm"
    }

    private val _chatList =
        liveData(viewModelScope.coroutineContext) { emit(chats.getChats()) }
    val chatList: LiveData<List<Chat>>
        get() = _chatList

    private val _publishChat = MutableLiveData<Chat>()
    val publishChat: LiveData<Chat>
        get() = _publishChat

    private val day =
        SimpleDateFormat(dayFormat, Locale.getDefault()).format(Date(System.currentTimeMillis()))
    private val _publishDay = MutableLiveData<String>(day)
    val publishDay: LiveData<String>
        get() = _publishDay

    private val time =
        SimpleDateFormat(timeFormat, Locale.getDefault()).format(Date(System.currentTimeMillis()))
    private val _publishTime = MutableLiveData<String>(time)
    val publishTime: LiveData<String>
        get() = _publishTime

    private val _postText = MutableLiveData<String>()
    val postText: LiveData<String>
        get() = _postText

    val takeBitmap = SingleLiveEvent<Bitmap>()

    private val _inputFiles = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val inputFiles: LiveData<MutableList<Uri>>
        get() = _inputFiles

    fun setPublishChat(chat: Chat) {
        _publishChat.value = chat
    }

    fun setPostText(text: String) {
        _postText.value = text
    }

    fun setPublishDay(day: String) {
        _publishDay.value = day
    }

    fun setPublishTime(time: String) {
        _publishTime.value = time
    }


    fun sendPost() {
        val inputImages = inputFiles.value ?: emptyList<String>()
        when (inputImages.size) {
            0, 1 -> sendSimplePost()
            else -> sendAlbum()
        }
    }

    private fun sendAlbum() {
        val content = createAlbum()
        val epoch = getEpochTime()
        val chatId = publishChat.value?.id ?: return
        viewModelScope.launch {
            messages.sendAlbum(
                chatId,
                content,
                epoch
            ).also { Log.d("NewPostViewModel", "sendPost: $it") }
        }
    }

    private fun createAlbum(): Array<InputMessageContent> {
        val list = mutableListOf<InputMessageContent>()
        for ((index, uri) in (inputFiles.value?:return list.toTypedArray()).withIndex()){
            val file = InputFileLocal(uri.recievePath()?:break)
            val photo =
                if (index == 0) {
                    InputMessagePhoto().apply { caption = getTextContent() }
                } else {
                    InputMessagePhoto()
                }
            photo.photo = file
            list += photo
        }

        return list.toTypedArray().also { Log.d("NewPostViewModel", "createAlbum: $it") }
    }

    private fun sendSimplePost() {
        val content = combineContent()
        val epoch = getEpochTime()
        val chatId = publishChat.value?.id ?: return
        viewModelScope.launch {
            messages.sendMessage(
                chatId,
                content,
                epoch
            ).also { Log.d("NewPostViewModel", "sendPost: $it") }
        }
    }

    private fun getEpochTime(): Int {
        val date = SimpleDateFormat("$dayFormat $timeFormat",Locale.getDefault())
        val cal = Calendar.getInstance()
        val timeZone = cal.timeZone
        Log.d("NewPostViewModel", "getEpochTime: $timeZone")
        date.timeZone = timeZone
        val day = publishDay.value ?: throw IllegalStateException("day null")
        val time = publishTime.value ?: throw IllegalStateException("time null")
        val timeGMT = date.parse("$day $time").time
        return (timeGMT / 1000).toInt() + 1
    }


    private fun combineContent(): InputMessageContent {
        val txt = getTextContent()
        val list = inputFiles.value as List<String>
        return when (list.size) {
            0 -> {
                InputMessageText(txt, false, false)
            }
            1 -> {
                val file = InputFileLocal(list[0])
                val photo = InputMessagePhoto()
                photo.photo = file
                photo.caption = txt
                photo
            }
            else -> throw IllegalStateException("files more than 1")
        }
    }

    private fun getTextContent(): FormattedText {
        val text = postText.value
        val txt = FormattedText(text, null)
        return txt
    }





    private fun Uri.recievePath():String?{
        val cursor: Cursor =
            context.getContentResolver().query(this, null, null, null, null) ?: return null
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    fun onFileChosen(uri: Uri) {
        val path = uri.recievePath() ?: return
        Log.d("NewPostViewModel", "onFileChosen: ${uri.path} $path")
        _inputFiles.value?.add(uri)
        _inputFiles.value= _inputFiles.value
    }
}