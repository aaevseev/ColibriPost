package ru.teamdroid.colibripost.di.modules

import dagger.Module
import dagger.Provides
import ru.teamdroid.colibripost.data.AccountRemote
import ru.teamdroid.colibripost.data.channels.ChannelsRemote
import ru.teamdroid.colibripost.remote.account.AccountRemoteImpl
import ru.teamdroid.colibripost.remote.account.AccountRequests
import ru.teamdroid.colibripost.remote.channels.ChannelsRemoteImpl
import ru.teamdroid.colibripost.remote.channels.ChatsRequests
import javax.inject.Singleton

@Module
class RemoteModule {


    @Singleton
    @Provides
    fun provideChannelsRemote(chatsRequests: ChatsRequests): ChannelsRemote {
        return ChannelsRemoteImpl(chatsRequests)
    }

    @Singleton
    @Provides
    fun provideAccountRemote(accountRequests: AccountRequests): AccountRemote {
        return AccountRemoteImpl(accountRequests)
    }


}