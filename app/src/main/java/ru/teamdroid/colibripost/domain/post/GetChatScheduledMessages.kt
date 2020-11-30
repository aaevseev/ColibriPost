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
) : UseCase<List<PostEntity>, List<Long>>() {

    override suspend fun run(params: List<Long>) = postRepository.getChatSchedulesMessages(params)

}