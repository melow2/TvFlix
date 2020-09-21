package com.khs.androidbaseproject.network.home

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * 1) Moshi를 사용하고, Adapter를 생성하여 데이터를 받아온다.
 * 2) @Parcelize로 데이터를 직렬화 한다.
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-09-21 오전 11:57
 **/
@Parcelize
@JsonClass(generateAdapter = true)
data class Episode(
    val show: Show,
    val id: Long,
    val url: String?,
    val name: String?,
    val season: Int?,
    val number: Int?,
    val airdate: String?,
    val airtime: String?,
    val runtime: Int?
) : Parcelable

