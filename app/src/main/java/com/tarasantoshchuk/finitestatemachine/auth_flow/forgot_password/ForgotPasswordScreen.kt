package com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password

import android.support.annotation.LayoutRes
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password.ForgotPasswordScreen.Data
import com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password.ForgotPasswordScreen.Events
import com.tarasantoshchuk.finitestatemachine.commons.BaseWorkflowScreen
import com.tarasantoshchuk.finitestatemachine.commons.CommonScreenData
import io.reactivex.Observable

class ForgotPasswordScreen(screenData: Observable<Data>, commonData: Observable<CommonScreenData>, eventHandler: Events) :
        BaseWorkflowScreen<Data, Events>(KEY, screenData, commonData, eventHandler) {
    companion object {
        val KEY: String = ForgotPasswordScreen::class.java.simpleName

        @LayoutRes
        val LAYOUT: Int = R.layout.forgot_password
    }

    interface Events {
        fun onSubmitEmail(email: String)
    }

    data class Data(val message: String)
}

