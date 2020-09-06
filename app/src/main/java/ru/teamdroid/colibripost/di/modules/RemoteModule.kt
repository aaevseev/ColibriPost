package ru.teamdroid.colibripost.di.modules

import dagger.Module
import dagger.Provides
import ru.teamdroid.colibripost.data.ChannelsRemote
import ru.teamdroid.colibripost.data.ChannelsRepositoryImpl
import ru.teamdroid.colibripost.remote.ChannelsRemoteImpl
import ru.teamdroid.colibripost.remote.Chats
import javax.inject.Singleton

@Module
class RemoteModule {


    @Singleton
    @Provides
    fun provideChannelsRemote(chats: Chats): ChannelsRemote{
        return ChannelsRemoteImpl(chats)
    }


}