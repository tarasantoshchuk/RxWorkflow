package com.tarasantoshchuk.finitestatemachine

import com.tarasantoshchuk.finitestatemachine.LoginScreen.LoginData
import com.tarasantoshchuk.finitestatemachine.LoginScreen.LoginEvents
import com.tarasantoshchuk.rx_workflow.WorkflowScreen
import io.reactivex.Observable

class LoginScreen(screenData: Observable<LoginData>, eventHandler: LoginEvents) :
        WorkflowScreen<LoginData, LoginEvents>(KEY, screenData, eventHandler) {
    companion object {
        val KEY: String = LoginScreen::class.java.simpleName
    }

    interface LoginEvents {
        fun onLoginPressed(email: String, password: String)
        fun onResetPassword(email: String)
    }

    data class LoginData(val message: String)
}

