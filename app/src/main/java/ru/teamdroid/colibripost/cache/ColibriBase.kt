package ru.teamdroid.colibripost.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.teamdroid.colibripost.domain.channels.ChannelEntity

@Database(entities = [ChannelEntity::class], version = 1)
abstract class ColibriDatabase: RoomDatabase() {

    abstract val channelsDao: ChannelsDao

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