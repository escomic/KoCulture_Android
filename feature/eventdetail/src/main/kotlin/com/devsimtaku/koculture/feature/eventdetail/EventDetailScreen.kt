package com.devsimtaku.koculture.feature.eventdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.devsimtaku.koculture.core.domain.model.CulturalEvent
import com.devsimtaku.koculture.core.ui.browser.openUrl
import com.devsimtaku.koculture.core.ui.map.CultureMap
import com.devsimtaku.koculture.core.ui.map.MapCoordinate
import com.devsimtaku.koculture.core.ui.map.getExternalMapDestination
import com.devsimtaku.koculture.core.ui.map.openExternalMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    culturalEvent: CulturalEvent,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        EventDetailContent(
            culturalEvent = culturalEvent,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun EventDetailContent(
    culturalEvent: CulturalEvent,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    model = culturalEvent.imageUrl.ifBlank { null },
                    contentDescription = culturalEvent.title,
                    contentScale = ContentScale.FillWidth,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DetailChip(text = culturalEvent.codeName.ifBlank { "문화행사" })
                DetailChip(text = if (culturalEvent.isFree) "무료" else "유료")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = culturalEvent.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )

            if (culturalEvent.date.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = culturalEvent.date,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            DetailSection(
                title = "행사 정보",
                rows = listOf(
                    DetailRowItem("기간", dateRange(culturalEvent)),
                    DetailRowItem("시간", culturalEvent.time),
                    DetailRowItem("장소", place(culturalEvent)),
                    DetailRowItem("요금", culturalEvent.fee),
                    DetailRowItem("대상", culturalEvent.targetUser),
                    DetailRowItem("문의", culturalEvent.inquiry),
                ),
            )

            DetailSection(
                title = "운영 정보",
                rows = listOf(
                    DetailRowItem("기관", culturalEvent.organizationName),
                    DetailRowItem("출연", culturalEvent.player),
                    DetailRowItem("프로그램", culturalEvent.program),
                    DetailRowItem("티켓", culturalEvent.ticket),
                    DetailRowItem("등록일", culturalEvent.registrationDate),
                ),
            )

            LocationSection(
                latitude = culturalEvent.latitude,
                longitude = culturalEvent.longitude,
                place = culturalEvent.place,
            )

            DetailSection(
                title = "링크",
                rows = listOf(
                    DetailRowItem(
                        label = "상세 URL",
                        value = culturalEvent.detailUrl,
                        onClick = { url -> context.openUrl(url) },
                    ),
                    DetailRowItem(
                        label = "기관 URL",
                        value = culturalEvent.organizationLink,
                        onClick = { url -> context.openUrl(url) },
                    ),
                ),
            )

            if (culturalEvent.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "소개",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = culturalEvent.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LocationSection(
    latitude: String,
    longitude: String,
    place: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coordinate = coordinateOrNull(
        latitude = latitude,
        longitude = longitude,
    )
    val placeText = place.trim()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp),
    ) {
        Text(
            text = "위치",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
        if (placeText.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = placeText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (coordinate == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "위치 정보를 표시할 수 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            CultureMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                coordinate = coordinate,
            )
            TextButton(
                onClick = {
                    val destination = context.getExternalMapDestination(
                        coordinate = coordinate,
                        label = placeText,
                    )
                    context.openExternalMap(destination)
                },
            ) {
                Text(text = "지도에서 크게 보기")
            }
        }
    }
}

@Composable
private fun DetailChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    AssistChip(
        modifier = modifier,
        onClick = {},
        label = {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}

@Composable
private fun DetailSection(
    title: String,
    rows: List<DetailRowItem>,
    modifier: Modifier = Modifier,
) {
    val visibleRows = rows.filter { row -> row.value.isNotBlank() }

    if (visibleRows.isEmpty()) {
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        visibleRows.forEachIndexed { index, row ->
            DetailRow(
                label = row.label,
                value = row.value,
                onClick = row.onClick?.let { onClick ->
                    { onClick(row.value) }
                },
            )
            if (index != visibleRows.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            modifier = Modifier.width(76.dp),
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (onClick == null) {
                        Modifier
                    } else {
                        Modifier.clickable(onClick = onClick)
                    },
                ),
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (onClick == null) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.primary
            },
            textDecoration = if (onClick == null) {
                TextDecoration.None
            } else {
                TextDecoration.Underline
            },
        )
    }
}

private class DetailRowItem(
    val label: String,
    value: String,
    val onClick: ((String) -> Unit)? = null,
) {
    val value: String = value.trim()
}

private fun dateRange(culturalEvent: CulturalEvent): String {
    return listOf(culturalEvent.startDate, culturalEvent.endDate)
        .filter { it.isNotBlank() }
        .distinct()
        .joinToString(separator = " ~ ")
        .ifBlank { culturalEvent.date }
}

private fun coordinateOrNull(
    latitude: String,
    longitude: String,
): MapCoordinate? {
    val latitudeValue = latitude.trim().toDoubleOrNull()
    val longitudeValue = longitude.trim().toDoubleOrNull()

    if (latitudeValue == null || longitudeValue == null) {
        return null
    }

    return MapCoordinate(
        latitude = latitudeValue,
        longitude = longitudeValue,
    )
}

private fun place(culturalEvent: CulturalEvent): String {
    return listOf(culturalEvent.district, culturalEvent.place)
        .filter { it.isNotBlank() }
        .joinToString(separator = " · ")
}
