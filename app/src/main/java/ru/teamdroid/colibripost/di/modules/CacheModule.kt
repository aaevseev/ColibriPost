package ru.teamdroid.colibripost.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.teamdroid.colibripost.cache.ColibriDatabase
import ru.teamdroid.colibripost.data.ChannelsCache
import javax.inject.Singleton

@Module
class CacheModule {

    @Provides
    @Singleton
    fun provideColibriDatabase(context:Context): ColibriDatabase{
        return ColibriDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideChannelsCache(colibriDatabase: ColibriDatabase): ChannelsCache{
        return colibriDatabase.channelsDao
    }
}