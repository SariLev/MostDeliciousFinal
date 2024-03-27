package com.example.mostdelicious.helpers

sealed class LoadingState {
    data object Loading: LoadingState()
    data object Loaded: LoadingState()
}