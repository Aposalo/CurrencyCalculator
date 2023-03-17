package aposalo.com.currencycalculator.util

import android.app.Activity
import com.google.android.play.core.review.ReviewManager

class GoogleManager {

    companion object {

         fun requestReviewInfo(manager: ReviewManager, activity: Activity) {
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    manager.launchReviewFlow(activity, task.result)
                }
            }
        }

    }
}