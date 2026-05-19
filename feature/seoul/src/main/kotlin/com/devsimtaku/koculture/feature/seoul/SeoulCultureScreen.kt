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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.devsimtaku.koculture.feature.seoul.contract.SeoulCultureCategory
import com.devsimtaku.koculture.feature.seoul.contract.SeoulUiEffect
import com.devsimtaku.koculture.feature.seoul.contract.SeoulUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeoulCultureScreen(
    modifier: Modifier = Modifier,
    viewModel: SeoulCultureViewModel = hiltViewModel(),
    onEventClick: (CulturalEvent) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val culturalEvents = viewModel.culturalEvents.collectAsLazyPagingItems()
    var showCategorySheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SeoulUiEffect.NavigateToDetail -> {
                    onEventClick(effect.culturalEvent)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.title,
                        fontWeight = FontWeight.Bold,
                    )
                },
            )
        },
    ) { innerPadding ->
        SeoulContent(
            modifier = Modifier.padding(innerPadding),
            selectedCategory = uiState.selectedCategory,
            isFreeOnly = uiState.isFreeOnly,
            culturalEvents = culturalEvents,
            onCategoryClick = {
                showCategorySheet = true
            },
            onFreeOnlyChange = { isFreeOnly ->
                viewModel.sendEvent(SeoulUiEvent.OnFreeOnlyChange(isFreeOnly))
            },
            onEventClick = { culturalEvent ->
                viewModel.sendEvent(SeoulUiEvent.OnEventClick(culturalEvent))
            },
        )
    }

    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showCategorySheet = false
            },
        ) {
            CategoryBottomSheet(
                selectedCategory = uiState.selectedCategory,
                onCategorySelect = { category ->
                    viewModel.sendEvent(SeoulUiEvent.OnCategorySelect(category))
                    showCategorySheet = false
                },
            )
        }
    }
}

@Composable
private fun SeoulContent(
    selectedCategory: SeoulCultureCategory,
    isFreeOnly: Boolean,
    culturalEvents: LazyPagingItems<CulturalEvent>,
    onCategoryClick: () -> Unit,
    onFreeOnlyChange: (Boolean) -> Unit,
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
                    top = 12.dp,
                    end = 20.dp,
                    bottom = 32.dp,
                ),
            ) {
                item {
                    FilterControls(
                        selectedCategory = selectedCategory,
                        isFreeOnly = isFreeOnly,
                        onCategoryClick = onCategoryClick,
                        onFreeOnlyChange = onFreeOnlyChange,
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
private fun FilterControls(
    selectedCategory: SeoulCultureCategory,
    isFreeOnly: Boolean,
    onCategoryClick: () -> Unit,
    onFreeOnlyChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onCategoryClick,
        ) {
            Text(
                text = selectedCategory.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Row(
            modifier = Modifier.height(48.dp),
        ) {
            Checkbox(
                checked = isFreeOnly,
                onCheckedChange = onFreeOnlyChange,
            )
            Text(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .align(androidx.compose.ui.Alignment.CenterVertically),
                text = "무료",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun CategoryBottomSheet(
    selectedCategory: SeoulCultureCategory,
    onCategorySelect: (SeoulCultureCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = "카테고리",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            contentPadding = PaddingValues(bottom = 28.dp),
        ) {
            items(
                items = SeoulCultureCategory.entries,
                key = { category -> category.name },
            ) { category ->
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onCategorySelect(category)
                    },
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = category.label,
                        color = if (category == selectedCategory) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = if (category == selectedCategory) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                    )
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
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
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
            Button(
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
        Button(
            onClick = onRetryClick,
        ) {
            Text(text = "다시 시도")
        }
    }
}
