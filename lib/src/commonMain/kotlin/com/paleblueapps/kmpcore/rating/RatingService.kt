package com.paleblueapps.kmpcore.rating

import com.paleblueapps.kmpcore.preferencesmanager.PreferencesManager
import com.paleblueapps.kmpcore.utilities.fromEpochMilliseconds
import com.paleblueapps.kmpcore.utilities.now
import com.paleblueapps.kmpcore.utilities.toEpochMilliseconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

/**
 * Interface for managing the app rating and feedback flow. It provides methods for configuring
 * dialog settings, tracking user actions, and initiating the rating process.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface RatingService {
    /**
     * Configures the settings for the rating system, such as the dialog configurations, snooze duration,
     * and minimum number of user actions required before asking for a review.
     *
     * @param ratingDialogConfig Configuration for the rating dialog, including its title, message,
     *                           and button text options.
     * @param feedbackDialogConfig Configuration for the feedback dialog, including its title, message,
     *                             and button text options.
     * @param snoozeDuration The minimum duration to wait before showing the rating prompt again.
     *                       Default value is 180 days.
     * @param minActionsNeededForAskingReview The minimum number of user actions required before
     *                                        the system initiates the review flow. Default value is 3.
     */
    fun configure(
        ratingDialogConfig: DialogConfig,
        feedbackDialogConfig: DialogConfig,
        snoozeDuration: Duration = 180.days,
        minActionsNeededForAskingReview: Int = 3,
    )

    /**
     * Logs a user action within the rating system.
     * This method is used to track user interactions, such as navigating through the app or performing key actions.
     * These actions contribute to determining when to prompt the user with a review or feedback dialogue.
     *
     * This function is suspendable, allowing it to be invoked in a coroutine context.
     */
    suspend fun logUserAction()

    /**
     * Initiates the rating flow, allowing users to provide feedback or rate the application.
     * The flow listens for user interactions and emits events based on their responses.
     *
     * @param listener A suspendable function that is called with a [RatingEvent] representing
     *                 the user's interaction during the rating flow. These events can include
     *                 actions such as clicking positive or negative buttons in the rating or
     *                 feedback dialogues.
     */
    suspend fun startRatingFlow(listener: suspend (RatingEvent) -> Unit)
}

enum class DialogResult {
    Positive,
    Negative,
}

sealed interface RatingEvent {
    data object OnRatingPositiveClick : RatingEvent
    data object OnRatingNegativeClick : RatingEvent
    data object OnFeedbackPositiveClick : RatingEvent
    data object OnFeedbackNegativeClick : RatingEvent
}

internal abstract class RealRatingService : RatingService {

    companion object {
        internal const val PREFERENCES_NAME = "kmp-preferences.preferences_pb"
        internal const val ENCRYPTED_PREFERENCES_NAME = "encrypted_kmp_preferences"
        private const val HAS_REVIEWED_APP_KEY = "has_reviewed_app"
        private const val LAST_PROMPT_FOR_REVIEW_MILLIS_KEY = "last_prompt_for_review_millis"
        private const val REVIEW_ACTIONS_COUNT_KEY = "review_actions_count"
    }

    internal abstract val preferencesManager: PreferencesManager
    private var ratingDialogConfig: DialogConfig = DialogConfig.RATE_US
    private var feedbackDialogConfig: DialogConfig = DialogConfig.FEEDBACK
    private var snoozeDuration: Duration = 180.days
    private var minActionsNeededForAskingReview: Long = 0

    internal abstract suspend fun showDialog(config: DialogConfig): DialogResult

    override fun configure(
        ratingDialogConfig: DialogConfig,
        feedbackDialogConfig: DialogConfig,
        snoozeDuration: Duration,
        minActionsNeededForAskingReview: Long,
    ) {
        this.ratingDialogConfig = ratingDialogConfig
        this.feedbackDialogConfig = feedbackDialogConfig
        this.snoozeDuration = snoozeDuration
        this.minActionsNeededForAskingReview = minActionsNeededForAskingReview
    }

    override suspend fun logUserAction() {
        setReviewActionsCount(getReviewActionsCount() + 1)
    }

    override suspend fun startRatingFlow(listener: suspend (RatingEvent) -> Unit) {
        if (getHasReviewedApp()) return
        getLastPromptForReviewMillis()?.let {
            val lastPromptForReviewDate = LocalDate.fromEpochMilliseconds(it)
            val daysSinceLastPrompt = lastPromptForReviewDate.daysUntil(LocalDate.now())
            if (daysSinceLastPrompt < snoozeDuration.inWholeDays) return
        }
        if (getReviewActionsCount() < minActionsNeededForAskingReview) return
        setLastPromptForReviewMillis(LocalDate.now().toEpochMilliseconds())

        when (showDialog(ratingDialogConfig)) {
            DialogResult.Positive -> {
                listener(RatingEvent.OnRatingPositiveClick)
                setHasReviewedApp(true)
            }
            DialogResult.Negative -> {
                listener(RatingEvent.OnRatingNegativeClick)
                when (showDialog(feedbackDialogConfig)) {
                    DialogResult.Positive -> {
                        listener(RatingEvent.OnFeedbackPositiveClick)
                    }
                    DialogResult.Negative -> {
                        listener(RatingEvent.OnFeedbackNegativeClick)
                    }
                }
            }
        }
    }

    private suspend fun getHasReviewedApp(): Boolean =
        preferencesManager.getBoolean(HAS_REVIEWED_APP_KEY) ?: false

    private suspend fun setHasReviewedApp(hasReviewedApp: Boolean) {
        preferencesManager.putBoolean(HAS_REVIEWED_APP_KEY, hasReviewedApp)
    }

    private suspend fun getLastPromptForReviewMillis(): Long? =
        preferencesManager.getLong(LAST_PROMPT_FOR_REVIEW_MILLIS_KEY)

    private suspend fun setLastPromptForReviewMillis(lastPromptForReviewMillis: Long?) {
        if (lastPromptForReviewMillis != null) {
            preferencesManager.putLong(LAST_PROMPT_FOR_REVIEW_MILLIS_KEY, lastPromptForReviewMillis)
        } else {
            preferencesManager.removeLong(LAST_PROMPT_FOR_REVIEW_MILLIS_KEY)
        }
    }

    private suspend fun getReviewActionsCount(): Int =
        preferencesManager.getInt(REVIEW_ACTIONS_COUNT_KEY) ?: 0

    private suspend fun setReviewActionsCount(reviewActionsCount: Int) {
        preferencesManager.putInt(REVIEW_ACTIONS_COUNT_KEY, reviewActionsCount)
    }
}
