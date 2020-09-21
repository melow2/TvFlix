package com.khs.androidbaseproject.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.khs.androidbaseproject.R
import com.khs.androidbaseproject.utils.GridItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), ShowsAdapter.Callback {

    private val homeViewModel:HomeViewModel by viewModels()
    private lateinit var showsAdapter: ShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        homeViewModel.onScreenCreated()
        homeViewModel.getHomeViewState().observe(this,{
            setViewState(it)
        })
    }

    private fun setViewState(homeViewState: HomeViewState?) {
        when (homeViewState) {
            is Loading -> setProgress(true)
            is NetworkError -> {
                setProgress(false)
                showError(homeViewState.message!!)
            }
            is Success -> {
                setProgress(false)
                showPopularShows(homeViewState.homeViewData)
            }
        }
    }

    override fun onFavoriteClicked(showViewData: HomeViewData.ShowViewData) {
        if (!showViewData.isFavoriteShow) {
            homeViewModel.addToFavorite(showViewData.show)
            Toast.makeText(this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show()
        } else {
            homeViewModel.removeFromFavorite(showViewData.show)
            Toast.makeText(this, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPopularShows(homeViewData: HomeViewData) {
        val gridLayoutManager = GridLayoutManager(this, NO_OF_COLUMNS)
        showsAdapter = ShowsAdapter(this)
        showsAdapter.updateList(homeViewData.episodes.toMutableList())
        popular_shows.apply {
            layoutManager = gridLayoutManager
            setHasFixedSize(true)
            adapter = showsAdapter
            val spacing = resources.getDimensionPixelSize(R.dimen.show_grid_spacing)
            addItemDecoration(GridItemDecoration(spacing, NO_OF_COLUMNS))
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    private fun setProgress(isLoading: Boolean) {
        if (isLoading) {
            showProgress()
        } else {
            hideProgress()
        }
    }

    private fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progress.visibility = View.GONE
    }

    companion object {
        private const val NO_OF_COLUMNS = 2
    }
}