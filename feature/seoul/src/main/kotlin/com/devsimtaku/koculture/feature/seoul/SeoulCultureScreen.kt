package com.devsimtaku.koculture.feature.seoul

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.devsimtaku.koculture.core.domain.model.CulturalEvent
import com.devsimtaku.koculture.feature.seoul.contract.SeoulUiEffect
import com.devsimtaku.koculture.feature.seoul.contract.SeoulUiEvent

@Composable
fun SeoulCultureScreen(
    modifier: Modifier = Modifier,
    viewModel: SeoulCultureViewModel = hiltViewModel(),
    onEventClick: (CulturalEvent) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val culturalEvents = viewModel.culturalEvents.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SeoulUiEffect.NavigateToDetail -> {
                    onEventClick(effect.culturalEvent)
                }
            }
        }
    }

    SeoulContent(
        modifier = modifier,
        title = uiState.title,
        culturalEvents = culturalEvents,
        onEventClick = { culturalEvent ->
            viewModel.sendEvent(SeoulUiEvent.OnEventClick(culturalEvent))
        },
    )
}

@Composable
private fun SeoulContent(
    title: String,
    culturalEvents: LazyPagingItems<CulturalEvent>,
    onEventClick: (CulturalEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val refreshState = culturalEvents.loadState.refresh

    when {
        refreshState is LoadState.Loading && culturalEvents.itemCount == 0 -> {
            LoadingContent(modifier = modifier)
        }

        refreshState is LoadState.Error && culturalEvents.itemCount == 0 -> {
            ErrorContent(
                modifier = modifier,
                message = refreshState.error.message ?: "문화행사 정보를 불러오지 못했습니다.",
                onRetryClick = culturalEvents::retry,
            )
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 24.dp,
                    end = 20.dp,
                    bottom = 32.dp,
                ),
            ) {
                item {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (culturalEvents.itemCount == 0) {
                    item {
                        EmptyContent()
                    }
                } else {
                    items(
                        count = culturalEvents.itemCount,
                        key = culturalEvents.itemKey { event ->
                            "${event.title}_${event.startDate}_${event.place}"
                        },
                    ) { index ->
                        culturalEvents[index]?.let { culturalEvent ->
                            CulturalEventListItem(
                                culturalEvent = culturalEvent,
                                onClick = { onEventClick(culturalEvent) },
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                when (val appendState = culturalEvents.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            AppendLoadingContent()
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            AppendErrorContent(
                                message = appendState.error.message ?: "추가 정보를 불러오지 못했습니다.",
                                onRetryClick = culturalEvents::retry,
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun CulturalEventListItem(
    culturalEvent: CulturalEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(128.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = culturalEvent.imageUrl.ifBlank { null },
                    contentDescription = culturalEvent.title,
                    contentScale = ContentScale.Crop,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AssistChip(
                        onClick = onClick,
                        label = {
                            Text(
                                text = culturalEvent.codeName.ifBlank { "문화행사" },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    AssistChip(
                        onClick = onClick,
                        label = {
                            Text(text = if (culturalEvent.isFree) "무료" else "유료")
                        },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = culturalEvent.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = culturalEvent.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = listOf(culturalEvent.district, culturalEvent.place)
                        .filter { it.isNotBlank() }
                        .joinToString(separator = " · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (culturalEvent.time.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = culturalEvent.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        text = "표시할 문화행사가 없습니다.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ErrorContent(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.material3.Button(
                onClick = onRetryClick,
            ) {
                Text(text = "다시 시도")
            }
        }
    }
}

@Composable
private fun AppendLoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AppendErrorContent(
    message: String,
    onRetryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Button(
            onClick = onRetryClick,
        ) {
            Text(text = "다시 시도")
        }
    }
}
