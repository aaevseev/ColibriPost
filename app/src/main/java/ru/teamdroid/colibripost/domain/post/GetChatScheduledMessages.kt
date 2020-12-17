package ru.teamdroid.colibripost.domain.post

import ru.teamdroid.colibripost.domain.UseCase
import ru.teamdroid.colibripost.domain.account.AccountEntity
import ru.teamdroid.colibripost.domain.account.AccountRepository
import ru.teamdroid.colibripost.domain.type.Either
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import javax.inject.Inject

class GetChatScheduledMessages @Inject constructor(
        private val postRepository: PostRepository
) : UseCase<List<PostEntity>, GetChatScheduledMessages.Params>() {

    override suspend fun run(params: Params) = postRepository.getChatSchedulesMessages(params.chatIds, params.calendarDay)

    data class Params(val chatIds: List<Long>, val calendarDay:Long)

}