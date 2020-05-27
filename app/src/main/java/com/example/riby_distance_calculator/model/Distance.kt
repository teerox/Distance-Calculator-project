package com.example.riby_distance_calculator.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "distance")
@Parcelize
data class Distance(
    @ColumnInfo(name = "startLongitude")
    var startLongitude:String = "",
    @ColumnInfo(name = "startLatitude")
    var startLatitude:String = "",
    @ColumnInfo(name = "stopLongitude")
    var stopLongitude:String = "",
    @ColumnInfo(name = "stopLatitude")
    var stopLatitude:String = ""


): Parcelable
{
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)var uid:Int = 0
}