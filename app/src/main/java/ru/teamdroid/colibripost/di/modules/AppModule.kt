package ru.teamdroid.colibripost.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.teamdroid.colibripost.data.AccountCache
import ru.teamdroid.colibripost.data.AccountRemote
import ru.teamdroid.colibripost.data.AccountRepositoryImpl
import ru.teamdroid.colibripost.data.channels.ChannelsCache
import ru.teamdroid.colibripost.data.channels.ChannelsRemote
import ru.teamdroid.colibripost.data.channels.ChannelsRepositoryImpl
import ru.teamdroid.colibripost.domain.account.AccountRepository
import ru.teamdroid.colibripost.domain.channels.ChannelsRepository
import ru.teamdroid.colibripost.remote.core.NetworkHandler
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Provides
    fun provideApplication(): Application {
        return app
    }

    @Provides
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }


    @Provides
    @Singleton
    fun provideChannelsRepository(remote: ChannelsRemote, cache: ChannelsCache, context: Context):
            ChannelsRepository {
        return ChannelsRepositoryImpl(remote, cache, NetworkHandler(context))
    }

    @Provides
    @Singleton
    fun provideAccountRepository(remote: AccountRemote, cache: AccountCache, context: Context):
            AccountRepository {
        return AccountRepositoryImpl(remote, cache, NetworkHandler(context))
    }
}