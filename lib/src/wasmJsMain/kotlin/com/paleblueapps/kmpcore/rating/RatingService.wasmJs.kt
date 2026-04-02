package com.paleblueapps.kmpcore.rating

import kotlin.time.Duration

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface RatingService {
    actual fun configure(
        ratingDialogConfig: DialogConfig,
        feedbackDialogConfig: DialogConfig,
        snoozeDuration: Duration,
        minActionsNeededForAskingReview: Int,
    )

    actual suspend fun logUserAction()

    actual suspend fun startRatingFlow(listener: suspend (RatingEvent) -> Unit)
}
