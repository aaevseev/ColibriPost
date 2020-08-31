package ru.teamdroid.colibripost.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.teamdroid.colibripost.data.ChannelsCache
import ru.teamdroid.colibripost.data.ChannelsRemote
import ru.teamdroid.colibripost.data.ChannelsRepositoryImpl
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
            ChannelsRepository{
        return ChannelsRepositoryImpl(remote, cache, NetworkHandler(context))
    }
}