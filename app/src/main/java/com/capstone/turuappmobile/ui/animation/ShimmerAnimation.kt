package com.capstone.turuappmobile.ui.animation

import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

class ShimmerAnimation {
    companion object{
        fun runShimmerAnimation(): ShimmerDrawable {
            val shimmer =
                Shimmer.AlphaHighlightBuilder()
                    .setDuration(1000)
                    .setBaseAlpha(0.7f)
                    .setHighlightAlpha(1f)
                    .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                    .setAutoStart(true)
                    .build()

            val shimmerDrawable = ShimmerDrawable().apply {
                setShimmer(shimmer)
            }

            return shimmerDrawable
        }

    }
}