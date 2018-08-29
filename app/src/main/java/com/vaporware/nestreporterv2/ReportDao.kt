package com.vaporware.nestreporterv2

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.content.Context


@Dao
interface ReportDao : BaseDao<Report> {
    @Query("SELECT * FROM reports WHERE report_id = :id")
    fun query(id: Int): LiveData<Report>
    @Query("SELECT COUNT(*) FROM reports")
    fun reportCount(): Int
    @Query("SELECT * FROM reports")
    fun getReports(): LiveData<List<Report>>
}

@Database(entities = [Report::class], version = 2)
@TypeConverters(Converters::class)
abstract class ReportDatabase: RoomDatabase() {
    abstract val reportDao: ReportDao

    companion object {
        private var instance: ReportDatabase? = null

        fun getInstance(context: Context): ReportDatabase {
            if (instance == null) {
                synchronized(ReportDatabase::class) {
//                    Todo: Don't forget to replace this with a real db.
                    instance = Room.inMemoryDatabaseBuilder(context,ReportDatabase::class.java).build()
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