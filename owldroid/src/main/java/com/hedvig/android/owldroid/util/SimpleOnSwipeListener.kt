package com.hedvig.android.owldroid.util

class SimpleOnSwipeListener(private var listener: (direction: Direction) -> Boolean) : OnSwipeListener() {
    override fun onSwipe(direction: Direction): Boolean {
        return listener(direction)
    }
}