package com.tarasantoshchuk.finitestatemachine.auth_flow.login

import android.widget.EditText
import butterknife.BindView
import butterknife.OnClick
import butterknife.Unbinder
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.utils.textString
import com.tarasantoshchuk.rx_workflow.butterknife.ButterknifeScreenCoordinator


class LoginScreenCoordinator(workflowScreen: LoginScreen) :
        ButterknifeScreenCoordinator<LoginScreen, LoginScreen.LoginEvents, LoginScreen.LoginData>(workflowScreen) {
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

    override fun attach() {
        screenData()
                .map {
                    it.message
                }
                .subscribe(emailText::setText)
    }
}
