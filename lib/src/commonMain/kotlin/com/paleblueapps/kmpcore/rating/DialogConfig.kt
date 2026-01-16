package com.paleblueapps.kmpcore.rating

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
            title = "We are sorry to hear that you are not super happy with the app.",
            message = "What could we do to improve your experience?",
            positiveButtonText = "Give feedback",
            negativeButtonText = "Not now",
        )
    }
}