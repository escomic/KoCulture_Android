package com.devsimtaku.koculture.core.ui.map

import android.view.MotionEvent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker

@Composable
internal fun NaverCultureMap(
    coordinate: MapCoordinate,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapViewState = remember { mutableStateOf<MapView?>(null) }
    val mapView = mapViewState.value

    DisposableEffect(lifecycle, mapView) {
        if (mapView == null) {
            onDispose { }
        } else {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> mapView.onStart()
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_STOP -> mapView.onStop()
                    else -> Unit
                }
            }

            lifecycle.addObserver(observer)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                mapView.onStart()
            }
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                mapView.onResume()
            }

            onDispose {
                lifecycle.removeObserver(observer)
                mapView.visibility = View.INVISIBLE
                mapView.onPause()
                mapView.onStop()
                mapView.onDestroy()
                if (mapViewState.value === mapView) {
                    mapViewState.value = null
                }
            }
        }
    }

    AndroidView(
        modifier = modifier.pointerInteropFilter { event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE,
                MotionEvent.ACTION_POINTER_DOWN,
                -> mapViewState.value?.parent?.requestDisallowInterceptTouchEvent(true)

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL,
                -> mapViewState.value?.parent?.requestDisallowInterceptTouchEvent(false)
            }
            false
        },
        factory = {
            MapView(it).also { createdMapView ->
                createdMapView.onCreate(null)
                mapViewState.value = createdMapView
            }
        },
        update = { updatedMapView ->
            updatedMapView.visibility = if (isVisible) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        },
    )

    DisposableEffect(mapView, coordinate) {
        if (mapView == null) {
            onDispose { }
        } else {
            val marker = Marker()
            val latLng = LatLng(
                coordinate.latitude,
                coordinate.longitude,
            )

            mapView.getMapAsync { naverMap ->
                naverMap.uiSettings.isLogoClickEnabled = false
                marker.position = latLng
                marker.map = naverMap
                naverMap.moveCamera(CameraUpdate.scrollTo(latLng))
            }

            onDispose {
                marker.map = null
            }
        }
    }
}
