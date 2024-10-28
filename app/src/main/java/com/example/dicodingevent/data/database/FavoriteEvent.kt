package com.dicoding.myapplication16.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favorite")
@Parcelize
data class FavoriteEvent(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "name")
    var name: String? = null, // Tambahkan kolom untuk nama

    @ColumnInfo(name = "mediaCover")
    var mediaCover: String? = null // Tambahkan kolom untuk media cover
) : Parcelable
