package com.paleblueapps.kmpcore.rating

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.paleblueapps.kmpcore.R
import com.paleblueapps.kmpcore.preferencesmanager.PreferencesManager
import kotlin.coroutines.resume
import kotlin.time.Duration
import kotlinx.coroutines.suspendCancellableCoroutine

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface RatingService {
    actual fun configure(
        ratingDialogConfig: DialogConfig,
        feedbackDialogConfig: DialogConfig,
        snoozeDuration: Duration,
        minActionsNeededForAskingReview: Long,
    )

    actual suspend fun logUserAction()
    actual suspend fun startRatingFlow(listener: suspend (RatingEvent) -> Unit)

    fun bind(activity: ComponentActivity)

    companion object {
        operator fun invoke(applicationContext: Context): RatingService =
            AndroidRatingService(applicationContext)
    }
}

internal class AndroidRatingService(private val context: Context) : RealRatingService() {

    private var activity: ComponentActivity? = null

    override val preferencesManager: PreferencesManager = PreferencesManager(
        context = context,
        preferencesFileName = PREFERENCES_NAME,
        encryptedPreferencesFileName = ENCRYPTED_PREFERENCES_NAME,
    )

    override fun bind(activity: ComponentActivity) {
        this.activity = activity

        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    this@AndroidRatingService.activity = null
                    source.lifecycle.removeObserver(this)
                }
            }
        }
        activity.lifecycle.addObserver(observer)
    }

    override suspend fun showDialog(config: DialogConfig): DialogResult =
        suspendCancellableCoroutine { continuation ->
            val activity = activity ?: throw IllegalStateException(
                "Activity is null. Make sure to call bind() before showing dialog.",
            )

            val dialogView = activity.layoutInflater.inflate(R.layout.dialog, null)
            val alertDialog = AlertDialog.Builder(activity).apply {
                setView(dialogView)
                setCancelable(false)
            }.create()

            dialogView.findViewById<TextView>(R.id.dialog_title).apply {
                text = config.title
            }
            dialogView.findViewById<TextView>(R.id.dialog_description).apply {
                if (config.message != null) {
                    text = config.message
                } else {
                    visibility = View.GONE
                }
            }

            dialogView.findViewById<TextView>(R.id.positiveButtonText).apply {
                text = config.positiveButtonText
                setOnClickListener {
                    continuation.resume(DialogResult.Positive)
                    alertDialog.dismiss()
                }
            }

            dialogView.findViewById<TextView>(R.id.negativeButtonText).apply {
                text = config.negativeButtonText
                setOnClickListener {
                    continuation.resume(DialogResult.Negative)
                    alertDialog.dismiss()
                }
            }

            alertDialog.show()

            continuation.invokeOnCancellation {
                alertDialog.dismiss()
            }
        }
}