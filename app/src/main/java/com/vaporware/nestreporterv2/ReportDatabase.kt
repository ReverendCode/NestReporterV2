package com.vaporware.nestreporterv2

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import kotlinx.coroutines.experimental.launch
import java.util.*


@Database(entities = [Report::class, Values::class], version = 2)
@TypeConverters(Converters::class)
abstract class ReportDatabase: RoomDatabase() {
    abstract val reportDao: ReportDao
    abstract val valuesDao: ValuesDao

    companion object {
        private var instance: ReportDatabase? = null

        fun getInstance(context: Context): ReportDatabase {
            if (instance == null) {
                synchronized(ReportDatabase::class) {
                    //                    Todo: Don't forget to replace this with a real db.
                    instance = Room.inMemoryDatabaseBuilder(context,ReportDatabase::class.java)
                            .addCallback(object : Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    launch {
                                        getInstance(context).reportDao.create(Report(
                                                0,
                                                1,
                                                1,
                                                NestType.Verified,
                                                "",
                                                false,
                                                false,
                                                false,
                                                Species.None,
                                                "",
                                                false,
                                                Date(0)
                                        ))
                                        getInstance(context).valuesDao.create(Values(
                                                0,1,2,1
                                        ))
                                    }
                                }
                            }).build()
//                    instance = Room.databaseBuilder(context,ReportDatabase::class.java,"foo")
//                            .fallbackToDestructiveMigration().build()
                }
            }
            return instance!!
        }
        fun destroyInstance() {
            instance = null
        }

    }
}