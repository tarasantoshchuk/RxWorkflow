package com.tarasantoshchuk.finitestatemachine.auth_flow.authorization

import android.support.annotation.LayoutRes
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.commons.BaseWorkflowScreen
import io.reactivex.Observable


class AuthorizationScreen: BaseWorkflowScreen<Any?, Any?>(KEY, Observable.empty(), Observable.empty(), null) {
    companion object {
        val KEY: String = AuthorizationScreen::class.java.simpleName

        @LayoutRes
        val LAYOUT: Int = R.layout.authorization
    }
}