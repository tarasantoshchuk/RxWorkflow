package com.tarasantoshchuk.finitestatemachine

import android.view.View
import android.widget.EditText
import com.tarasantoshchuk.finitestatemachine.utils.textString
import com.tarasantoshchuk.rx_workflow.ScreenCoordinator


class LoginScreenCoordinator(mWorkflowScreen: LoginScreen) :
        ScreenCoordinator<LoginScreen, LoginScreen.LoginEvents, LoginScreen.LoginData>(mWorkflowScreen) {

    lateinit var loginButton: View
    lateinit var resetPassword: View

    lateinit var emailText: EditText
    lateinit var passwordText: EditText


    override fun attach(view: View?) {
        view?.apply {
            loginButton = findViewById(R.id.login)
            loginButton.setOnClickListener {
                eventsHandler().onLoginPressed(emailText.textString(), passwordText.textString())
            }

            resetPassword = findViewById(R.id.reset_password)
            resetPassword.setOnClickListener {
                eventsHandler().onResetPassword(emailText.textString())
            }

            emailText = findViewById(R.id.et_email)
            passwordText = findViewById(R.id.et_password)

            screenData()
                    .map {
                        it.message
                    }
                    .subscribe(emailText::setText)
        }
    }
}
