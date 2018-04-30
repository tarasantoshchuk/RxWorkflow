package com.tarasantoshchuk.finitestatemachine.auth_flow.login

import android.view.View
import android.widget.EditText
import butterknife.BindView
import butterknife.OnClick
import butterknife.Unbinder
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.commons.BaseScreenCoordinator
import com.tarasantoshchuk.finitestatemachine.utils.textString


class LoginScreenCoordinator(workflowScreen: LoginScreen) :
        BaseScreenCoordinator<LoginScreen, LoginScreen.LoginEvents, LoginScreen.LoginData>(workflowScreen) {
    lateinit var unbinder: Unbinder

    @BindView(R.id.et_email)
    lateinit var emailText: EditText
    @BindView(R.id.et_password)
    lateinit var passwordText: EditText

    @OnClick(R.id.login)
    fun onLoginClick() {
        eventsHandler().onLoginPressed(emailText.textString(), passwordText.textString())
    }

    @OnClick(R.id.reset_password)
    fun onResetClick() {
        eventsHandler().onResetPassword(emailText.textString())
    }

    override fun attach(view: View) {
        super.attach(view)

        screenData()
                .map {
                    it.message
                }
                .subscribe(emailText::setText)
    }
}
