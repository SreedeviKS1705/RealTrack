package com.qaptive.core.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.qaptive.core.constants.Actions
import com.qaptive.core.ktx.AppCompatActivityKtx.enterFullScreen
import com.qaptive.core.ktx.AppCompatActivityKtx.exitFullScreen
import com.qaptive.core.models.Action
import com.qaptive.core.models.BaseActivityViewModel
import com.qaptive.core.models.LoadingMessageData
import com.qaptive.core.models.MessageData
import com.qaptive.core.utils.EventObserver

abstract class BaseActivity : AppCompatActivity() {

    var pendingNavigationIntent: Intent? = null
    var pendingNavigationActivityClass: Class<*>? = null
    var pendingNavigationFinishCurrent = false

    var isPaused = false

    private val viewModel: BaseActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.action.observe(this, EventObserver {
            onPerformAction(it)
        })
        viewModel.infoMessage.observe(this, EventObserver {
            onInfoMessage(it)
        })
        viewModel.loading.observe(this, EventObserver {
            onLoadingMessage(it)
        })
    }

    fun actionPerformed(action: Action) {
        viewModel.actionPerformed(action)
    }

    abstract fun onLoadingMessage(messageData: LoadingMessageData)
    abstract fun onInfoMessage(messageData: MessageData)

    abstract fun onLogout()

    open fun onPerformAction(action: Action) {
        when (action.task.action) {
            Actions.ENTER_FULLSCREEN -> {
                enterFullScreen(true)
            }
            Actions.EXIT_FULLSCREEN -> {
                exitFullScreen()
            }
            Actions.ACTION_LOGOUT -> {
                onLogout()
            }
        }
    }

    open fun onNavigateToActivity(intent: Intent, finishCurrent: Boolean) {
        if (isPaused) {
            pendingNavigationIntent = intent
            pendingNavigationFinishCurrent = finishCurrent
            return
        }
        startActivity(intent)
        if (finishCurrent)
            finish()
    }

    open fun onNavigateToActivityClass(activityClass: Class<*>, finishCurrent: Boolean) {
        if (isPaused) {
            pendingNavigationActivityClass = activityClass
            pendingNavigationFinishCurrent = finishCurrent
            return
        }
        val intent = Intent(this, activityClass)
        startActivity(intent)
        if (finishCurrent)
            finish()
    }
}