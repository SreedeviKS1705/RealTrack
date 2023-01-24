package com.qaptive.core.viewmodel

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.qaptive.core.R
import com.qaptive.core.models.*

abstract class BaseViewModel : ViewModel() {
    private val infoMessageLiveData = MutableLiveData<Event<MessageData>>()
    val infoMessage: LiveData<Event<MessageData>> = infoMessageLiveData

    private val actionLiveData = MutableLiveData<Event<Action>>()
    val action: LiveData<Event<Action>> = actionLiveData

    private val loadingLiveData = MutableLiveData<Event<LoadingMessageData>>()
    val loading: LiveData<Event<LoadingMessageData>> = loadingLiveData

    private val navDirectionsLiveData = MutableLiveData<Event<NavDirections>>()
    val navDirections: LiveData<Event<NavDirections>> = navDirectionsLiveData

    private val navigateLiveData = MutableLiveData<Event<NavigationData>>()
    val navigate: LiveData<Event<NavigationData>> = navigateLiveData

    private val upNavigationLiveData = MutableLiveData<Event<Boolean>>()
    val upNavigation: LiveData<Event<Boolean>> = upNavigationLiveData

    @Deprecated("Remove")
    private val activityNavigationLiveData = MutableLiveData<Event<ActivityNavigationModel>>()

    @Deprecated("Remove")
    val activityNavigation:LiveData<Event<ActivityNavigationModel>> = activityNavigationLiveData

//    init {
//        hideLoading()
//    }

    fun navigate(@IdRes id: Int, bundle: Bundle? = null) {
        navigateLiveData.postValue(Event(NavigationData(id, bundle)))
    }

    fun navigateUp() {
        upNavigationLiveData.postValue(Event(true))
    }

    fun navigate(navigationActionId: NavDirections) {
        navDirectionsLiveData.postValue(Event(navigationActionId))
    }

    fun showInfoMessage(message: MessageData) {
        infoMessageLiveData.value = Event(message)
    }

    open fun showLoading(message: LoadingMessageData) {
        loadingLiveData.value = Event(message)
    }

    fun performAction(action: Action) {
        actionLiveData.value = Event(action)
    }

    fun showLoading(@StringRes message: Int = R.string.loading) {
        val messageData = LoadingMessageData()
        messageData.isLoading = true
        messageData.messageRes = message
        showLoading(messageData)
    }

    fun hideLoading() {
        val messageData = LoadingMessageData()
        messageData.isLoading = false
        showLoading(messageData)
    }

    @Deprecated("Remove")
    fun onNavigateToActivity(intent: Intent, finishCurrent:Boolean=false)
    {
        val navigationModel=ActivityNavigationModel()
        navigationModel.intent=intent
        navigationModel.finishCurrent=finishCurrent
        activityNavigationLiveData.postValue(Event(navigationModel))
    }

    @Deprecated("Remove")
    fun onNavigateToActivity(activityClass:Class<*>,finishCurrent:Boolean=false)
    {
        val navigationModel= ActivityNavigationModel()
        navigationModel.activityClass=activityClass
        navigationModel.finishCurrent=finishCurrent
        activityNavigationLiveData.postValue(Event(navigationModel))
    }
}