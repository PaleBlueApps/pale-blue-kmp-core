package com.paleblueapps.kmpcore.rating

import com.paleblueapps.kmpcore.preferencesmanager.PreferencesManager
import com.paleblueapps.kmpcore.utilities.fromEpochMilliseconds
import com.paleblueapps.kmpcore.utilities.now
import com.paleblueapps.kmpcore.utilities.toEpochMilliseconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

data class DialogConfig(
    val title: String,
    val message: String?,
    val positiveButtonText: String,
    val negativeButtonText: String,
) {

    companion object {
        val RATE_US = DialogConfig(
            title = "How do you like App?",
            message = null,
            positiveButtonText = "\uD83E\uDEF6 I really love it",
            negativeButtonText = "\uD83D\uDE15 It could be better",
        )

        val FEEDBACK = DialogConfig(
            title = "We are sorry to hear that you are not super happy with Billin.",
            message = "What could we do to improve your experience?",
            positiveButtonText = "Give feedback",
            negativeButtonText = "Not now",
        )
    }
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

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface RatingService {
    fun configure(
        ratingDialogConfig: DialogConfig,
        feedbackDialogConfig: DialogConfig,
        snoozeDuration: Duration = 180.days,
        minActionsNeededForAskingReview: Long = 3,
    )
    suspend fun logUserAction()
    suspend fun startRatingFlow(listener: suspend (RatingEvent) -> Unit)
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
