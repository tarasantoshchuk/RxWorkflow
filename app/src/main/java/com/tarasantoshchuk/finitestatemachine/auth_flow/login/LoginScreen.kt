package com.tarasantoshchuk.finitestatemachine.auth_flow.login

import android.support.annotation.LayoutRes
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginScreen.LoginData
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginScreen.LoginEvents
import com.tarasantoshchuk.finitestatemachine.commons.BaseWorkflowScreen
import com.tarasantoshchuk.finitestatemachine.commons.CommonScreenData
import io.reactivex.Observable

class LoginScreen(screenData: Observable<LoginData>, commonData: Observable<CommonScreenData>, eventHandler: LoginEvents) :
        BaseWorkflowScreen<LoginData, LoginEvents>(KEY, screenData, commonData, eventHandler) {
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

