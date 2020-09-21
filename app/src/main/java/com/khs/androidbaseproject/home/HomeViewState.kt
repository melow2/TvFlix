package com.khs.androidbaseproject.home


/**
 * 1) sealed class는 클래스들을 묶은 클래스.
 * 2) enum class의 확장형
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-09-21 오후 5:07
 **/
sealed class HomeViewState
data class NetworkError(val message: String?) : HomeViewState()
object Loading : HomeViewState()
data class Success(val homeViewData: HomeViewData) : HomeViewState()