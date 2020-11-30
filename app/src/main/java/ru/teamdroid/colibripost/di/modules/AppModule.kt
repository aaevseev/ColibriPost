package ru.teamdroid.colibripost.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.teamdroid.colibripost.data.account.*
import ru.teamdroid.colibripost.data.account.auth.AuthRemote
import ru.teamdroid.colibripost.data.account.auth.AuthRepositoryImpl
import ru.teamdroid.colibripost.data.channels.ChannelsCache
import ru.teamdroid.colibripost.data.channels.ChannelsRemote
import ru.teamdroid.colibripost.data.channels.ChannelsRepositoryImpl
import ru.teamdroid.colibripost.data.post.PostRemote
import ru.teamdroid.colibripost.data.post.PostRepositoryImpl
import ru.teamdroid.colibripost.domain.account.AccountRepository
import ru.teamdroid.colibripost.domain.account.auth.AuthRepository
import ru.teamdroid.colibripost.domain.channels.ChannelsRepository
import ru.teamdroid.colibripost.domain.post.PostRepository
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

    @Provides
    @Singleton
    fun provideAuthRepository(remote: AuthRemote, context: Context): AuthRepository {
        return AuthRepositoryImpl(remote, NetworkHandler(context))
    }

    @Provides
    @Singleton
    fun providePostRepository(remote: PostRemote, context: Context): PostRepository {
        return PostRepositoryImpl(remote, NetworkHandler(context))
    }
}