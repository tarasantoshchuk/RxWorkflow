package com.tarasantoshchuk.finitestatemachine.auth_flow.login

import android.support.annotation.LayoutRes
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginScreen.LoginData
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginScreen.LoginEvents
import com.tarasantoshchuk.rx_workflow.WorkflowScreen
import io.reactivex.Observable

class LoginScreen(screenData: Observable<LoginData>, eventHandler: LoginEvents) :
        WorkflowScreen<LoginData, LoginEvents>(KEY, screenData, eventHandler) {
    companion object {
        val KEY: String = LoginScreen::class.java.simpleName

        @LayoutRes
        val LAYOUT: Int = R.layout.login
    }

    interface LoginEvents {
        fun onLoginPressed(email: String, password: String)
        fun onResetPassword(email: String)
    }

    data class LoginData(val message: String)
}

