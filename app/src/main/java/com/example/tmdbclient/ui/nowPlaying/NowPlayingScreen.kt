package com.example.tmdbclient.ui.nowPlaying

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.tmdbclient.R
import com.example.data.model.Movie
import com.example.tmdbclient.utils.Constants.TMDB_IMAGE_BASE_URL

@Composable
fun NowPlayingMoviesScreen(
    viewModel: NowPlayingViewModel,
    onMovieClick: (Int) -> Unit,
) {

    val lazyMovieItems: LazyPagingItems<Movie> = viewModel.moviesFiltered.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.now_playing_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(dimensionResource(R.dimen.default_content_padding))
        )
        if (lazyMovieItems.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            label = { Text("Search Movies by Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(dimensionResource(R.dimen.size_extra_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.size_extra_small)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.size_extra_small))
        ) {
            items(
                count = lazyMovieItems.itemCount,
//                key = lazyMovieItems.itemKey { it.id } // id from API duplicated
            ) { index ->
                val movie = lazyMovieItems[index]
                if (movie != null) {
                    MovieItem(movie = movie, onMovieClick = { onMovieClick(movie.id) })
                } else {
                    // shimmer effect or a simple placeholder here
                    Spacer(
                        modifier = Modifier
                            .aspectRatio(2f / 3f)
                            .fillMaxWidth()
                    )
                }
            }

            // Handle loading state for append (when scrolling down)
            lazyMovieItems.loadState.append.let {
                when (it) {
                    is LoadState.Loading -> {
                        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(R.dimen.default_content_padding))
                            ) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }

                    is LoadState.Error -> {
                        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(R.dimen.default_content_padding))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Error loading more movies", color = MaterialTheme.colorScheme.error)
                                    Button(onClick = { lazyMovieItems.retry() }) {
                                        Text(text = stringResource(R.string.default_retry))
                                    }
                                }
                            }
                        }
                    }

                    else -> Unit // NotLoading or EndOfPaginationReached
                }
            }
        }

        // Handle error state for refresh
        if (lazyMovieItems.loadState.refresh is LoadState.Error) {
            val error = (lazyMovieItems.loadState.refresh as LoadState.Error).error
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.default_content_padding))
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Failed to load movies: ${error.localizedMessage ?: "Unknown error"}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.size_extra_small)))
                    Button(onClick = { lazyMovieItems.refresh() }) {
                        Text(text = stringResource(R.string.default_retry))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieItem(
    movie: Movie,
    onMovieClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clickable(onClick = onMovieClick),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.default_elevation))
    ) {
        Column {
            GlideImage(
                model = TMDB_IMAGE_BASE_URL + movie.posterPath,
                failure = placeholder(R.drawable.baked_goods_1),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Text(
                text = movie.title,
                modifier = Modifier.padding(dimensionResource(R.dimen.default_content_padding)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}