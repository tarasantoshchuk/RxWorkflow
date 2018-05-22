package com.tarasantoshchuk.finitestatemachine.auth_flow.logged_in

import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.auth_flow.AuthorizationBrick.UserData
import com.tarasantoshchuk.finitestatemachine.auth_flow.logged_in.LoggedInScreen.Events
import com.tarasantoshchuk.finitestatemachine.commons.BaseWorkflowScreen
import com.tarasantoshchuk.finitestatemachine.commons.CommonScreenData
import io.reactivex.Observable


class LoggedInScreen(screenData: Observable<UserData>, commonData: Observable<CommonScreenData>, events: Events): BaseWorkflowScreen<UserData, Events>(KEY, screenData, commonData, events) {
    companion object {
        val KEY: String = LoggedInScreen::class.java.simpleName
        const val LAYOUT = R.layout.logged_in
    }

    interface Events {
        fun onLogOut()
        fun onContinue()
    }
}