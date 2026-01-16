package com.paleblueapps.kmpcore.rating

import com.paleblueapps.kmpcore.preferencesmanager.PreferencesManager
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIApplication

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

    companion object {
        operator fun invoke(): RatingService = IosRatingService()
    }
}

internal class IosRatingService : RealRatingService() {

    override val preferencesManager: PreferencesManager = PreferencesManager(
        preferencesFileName = PREFERENCES_NAME,
        encryptedPreferencesFileName = ENCRYPTED_PREFERENCES_NAME,
    )

    override suspend fun showDialog(config: DialogConfig): DialogResult =
        suspendCancellableCoroutine { continuation ->
            val alertController = UIAlertController.alertControllerWithTitle(
                title = config.title,
                message = config.message,
                preferredStyle = UIAlertControllerStyleActionSheet,
            )

            alertController.addAction(
                UIAlertAction.actionWithTitle(
                    title = config.positiveButtonText,
                    style = UIAlertActionStyleDefault,
                ) { _ ->
                    continuation.resume(DialogResult.Positive)
                    alertController.dismissViewControllerAnimated(true, completion = null)
                },
            )

            alertController.addAction(
                UIAlertAction.actionWithTitle(
                    title = config.negativeButtonText,
                    style = UIAlertActionStyleDefault,
                ) { _ ->
                    continuation.resume(DialogResult.Negative)
                    alertController.dismissViewControllerAnimated(true, completion = null)
                },
            )

            // Present the alert
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(
                alertController,
                animated = true,
                completion = null,
            )

            continuation.invokeOnCancellation {
                alertController.dismissViewControllerAnimated(true, completion = null)
            }
        }
}