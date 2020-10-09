package ru.teamdroid.colibripost.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.account.AccountEntity

@Database(entities = [ChannelEntity::class, AccountEntity::class], version = 2)
abstract class ColibriDatabase: RoomDatabase() {

    abstract val channelsDao: ChannelsDao
    abstract val accountDao: AccountDao

    companion object{
        @Volatile //актуальность объекта во всех потоках
        private var INSTANCE: ColibriDatabase? = null


        fun getInstance(context: Context): ColibriDatabase{

            var instance = INSTANCE

            if(instance == null){
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    ColibriDatabase::class.java,
                    "chat_database" //если потребуется миграция то структура БД
                    // будет изменена но данные буду потеряны
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }

            return instance
        }
    }
}