package com.vaporware.nestreporterv2

import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Update


interface BaseDao<T> {
    @Insert
    fun create(entity: T): Long
    @Update
    fun update(entity: T)
    @Delete
    fun delete(entity: T)
}