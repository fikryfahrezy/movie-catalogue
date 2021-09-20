package com.dicoding.picodiploma.moviecatalogue.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private const val RESOURCES = "GLOBAL"
    val espressoTestIdlingResource = CountingIdlingResource(RESOURCES)

    fun increment() {
        espressoTestIdlingResource.increment()
    }

    fun decrement() {
        espressoTestIdlingResource.decrement()
    }
}