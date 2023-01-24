package com.qaptive.core.models

import android.content.Intent

@Deprecated("Remove")
class ActivityNavigationModel {
    var intent: Intent? = null
    var finishCurrent: Boolean = false
    var activityClass: Class<*>? = null
}