package com.khs.androidbaseproject.home

import com.khs.androidbaseproject.network.home.Show


data class HomeViewData(val episodes: List<EpisodeViewData>) {

    data class EpisodeViewData(
        val id: Long,
        val showViewData: ShowViewData,
        val url: String?,
        val name: String?,
        val season: Int?,
        val number: Int?,
        val airdate: String?,
        val airtime: String?,
        val runtime: Int?
    )

    data class ShowViewData(
        val show: Show,
        val isFavoriteShow: Boolean
    )
}