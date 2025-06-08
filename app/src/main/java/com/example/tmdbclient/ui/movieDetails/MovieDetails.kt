package com.example.tmdbclient.ui.movieDetails

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.tmdbclient.R
import com.example.data.model.MovieDetails
import com.example.tmdbclient.utils.Constants.TMDB_IMAGE_BASE_URL
import com.example.tmdbclient.utils.Constants.TMDB_IMAGE_ORIGINAL
import java.util.Locale
import kotlin.math.min

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    onNavigateUp: () -> Unit,
) {
    val uiState by viewModel.movieDetailUiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is MovieDetailUiState.Error -> {
                ErrorState(
                    errorMessage = (uiState as MovieDetailUiState.Error).errorMessage,
                    onBackClick = onNavigateUp,
                    onRetry = {}
                )
            }

            MovieDetailUiState.Loading -> {
                LoadingState(onBackClick = onNavigateUp)
            }

            is MovieDetailUiState.Success -> {
                MovieDetailsContent(
                    movie = (uiState as MovieDetailUiState.Success).movieDetails,
                    onBackClick = onNavigateUp
                )
            }
        }
    }

}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MovieDetailsContent(
    movie: MovieDetails,
    onBackClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    // Parallax effect calculation
    val parallaxOffset = with(density) {
        (scrollState.value * 0.5f).toDp()
    }

    // Backdrop fade effect
    val backdropAlpha by animateFloatAsState(
        targetValue = 1f - min(scrollState.value / 1000f, 0.7f),
        label = "backdrop_alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Backdrop Image with Parallax
        GlideImage(
            model = TMDB_IMAGE_ORIGINAL + movie.backdropPath,
            contentDescription = "Movie backdrop",
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.details_movie_backdrop_height))
                .offset(y = -parallaxOffset)
                .graphicsLayer(alpha = backdropAlpha),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        startY = 0f,
                        endY = 1200f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top spacing for backdrop
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.details_movie_card_top_spacing)))

            // Main Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.default_content_padding)),
                shape = RoundedCornerShape(topStart = dimensionResource(R.dimen.spacer_large), topEnd = dimensionResource(R.dimen.spacer_large)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.size_extra_small))
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacer_large))
                ) {
                    // Movie Poster and Basic Info Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.default_content_padding))
                    ) {
                        // Movie Poster
                        Card(
                            modifier = Modifier.size(
                                width = dimensionResource(R.dimen.details_movie_poster_width),
                                height = dimensionResource(R.dimen.details_movie_content_height)
                            ),
                            shape = RoundedCornerShape(dimensionResource(R.dimen.spacer_small)),
                            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.default_elevation))
                        ) {
                            GlideImage(
                                model = TMDB_IMAGE_BASE_URL + movie.posterPath,
                                contentDescription = "Movie poster",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Title and Score Column
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(dimensionResource(R.dimen.details_movie_content_height)),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                // Main Title
                                Text(
                                    text = movie.title,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                // Original Title (if different)
                                if (movie.originalTitle != movie.title) {
                                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.default_elevation)))
                                    Text(
                                        text = movie.originalTitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Release Date
                                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.size_extra_small)))
                                Text(
                                    text = movie.releaseDate,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Score Section
                            ScoreSection(score = movie.voteAverage.toFloat())
                        }
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_large)))

                    // Description Section
                    Text(
                        text = stringResource(R.string.overview),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_small)))

                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_large)))
                }
            }
        }

        // Top App Bar with Back Button
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.size_extra_small))
                        .background(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingState(onBackClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    // Shimmer effect animation
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    // Pulsing animation for loading indicator
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Shimmer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = shimmerAlpha)
                )
        )

        // Loading indicator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = dimensionResource(R.dimen.details_movie_poster_width)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale),
                shape = RoundedCornerShape(dimensionResource(R.dimen.default_content_padding)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.size_extra_small))
            ) {
                Row(
                    modifier = Modifier.padding(dimensionResource(R.dimen.default_content_padding)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacer_small))
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimensionResource(R.dimen.spacer_large)),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                    Text(
                        text = "Loading movie details...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Back button
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.size_extra_small))
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorState(
    errorMessage: String,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacer_large)),
            shape = RoundedCornerShape(dimensionResource(R.dimen.default_content_padding)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.size_extra_small))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.spacer_large)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size))
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.default_content_padding)))

                Text(
                    text = "Oops! Something went wrong",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.size_extra_small)))

                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacer_large)))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacer_small))
                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(stringResource(R.string.default_back))
                    }

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                            contentColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(stringResource(R.string.default_retry))
                    }
                }
            }
        }

        // Back button in top bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.size_extra_small))
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.default_back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun ScoreSection(score: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.size_extra_small))
    ) {
        // Score Circle
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.score_size))
                .background(
                    color = when {
                        score >= 7.0f -> Color(0xFF4CAF50)
                        score >= 5.0f -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${(score * 10).toInt()}%",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = Color.White
            )
        }

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.size_extra_half_small))
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(dimensionResource(R.dimen.spacer_large))
                )
                Text(
                    text = String.format(locale = Locale.US, "%.1f", score),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = stringResource(R.string.user_score),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}