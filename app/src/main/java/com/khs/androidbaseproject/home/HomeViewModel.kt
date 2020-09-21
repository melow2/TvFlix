package com.khs.androidbaseproject.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khs.androidbaseproject.db.favouriteshow.FavoriteShow
import com.khs.androidbaseproject.favorite.FavoriteShowsRepository
import com.khs.androidbaseproject.network.TvMazeApi
import com.khs.androidbaseproject.network.home.Episode
import com.khs.androidbaseproject.network.home.Show
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel @ViewModelInject constructor(
    private val tvMazeApi: TvMazeApi,
    private val favoriteShowsRepository: FavoriteShowsRepository
):ViewModel() {
    private val homeViewStateLiveData: MutableLiveData<HomeViewState> = MutableLiveData()
    val country: String get() = COUNTRY_US
    private val coroutineExceptionHandler= CoroutineExceptionHandler { _, exception -> onError(exception) }

    fun onScreenCreated() {
        homeViewStateLiveData.value = Loading
        viewModelScope.launch(coroutineExceptionHandler){
            withContext(Dispatchers.IO){
                val favoriteShowIds = favoriteShowsRepository.allFavoriteShowIds()
                val episodes = tvMazeApi.getCurrentSchedule(COUNTRY_US, currentDate)
                withContext(Dispatchers.Main){
                    homeViewStateLiveData.value = Success(HomeViewData(getShowsWithFavorites(episodes,favoriteShowIds)))
                }
            }
        }
    }

    private fun onError(throwable: Throwable) {
        homeViewStateLiveData.value = NetworkError(throwable.message)
        Timber.e(throwable)
    }

    private fun getShowsWithFavorites(
        episodes:List<Episode>,
        favoriteShowIds:List<Long>
    ): List<HomeViewData.EpisodeViewData>{
        val episodeViewDataList = ArrayList<HomeViewData.EpisodeViewData>(episodes.size)
        for(episode in episodes){
            val show = episode.show
            val showViewData = if(favoriteShowIds.contains(show.id)){
                HomeViewData.ShowViewData(show,true)
            }else{
                HomeViewData.ShowViewData(show,false)
            }
            val episodeViewData = HomeViewData.EpisodeViewData(
                id = episode.id,
                showViewData = showViewData, url = episode.url, name = episode.name,
                season = episode.season, number = episode.number, airdate = episode.airdate,
                airtime = episode.airtime, runtime = episode.runtime
            )
            episodeViewDataList.add(episodeViewData)
        }
        return episodeViewDataList
    }

    fun getHomeViewState(): LiveData<HomeViewState> {
        return homeViewStateLiveData
    }

    fun addToFavorite(show: Show) {
        favoriteShowsRepository.insertShowIntoFavorites(show)
    }

    fun removeFromFavorite(show: Show) {
        favoriteShowsRepository.removeShowFromFavorites(show)
    }


    companion object {
        private const val COUNTRY_US = "US"
        private const val QUERY_DATE_FORMAT = "yyyy-MM-dd"

        private val currentDate: String
            get() {
                val simpleDateFormat = SimpleDateFormat(QUERY_DATE_FORMAT, Locale.US)
                val calendar = Calendar.getInstance()
                return simpleDateFormat.format(calendar.time)
            }
    }
}