package com.tarasantoshchuk.finitestatemachine.auth_flow

import com.tarasantoshchuk.finitestatemachine.auth_flow.authorization.AuthorizationScreen
import com.tarasantoshchuk.finitestatemachine.auth_flow.authorization.AuthorizationScreenCoordinator
import com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password.ForgotPasswordScreen
import com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password.ForgotPasswordScreenCoordinator
import com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password.ForgotPasswordTransition
import com.tarasantoshchuk.finitestatemachine.auth_flow.logged_in.LoggedInCoordinator
import com.tarasantoshchuk.finitestatemachine.auth_flow.logged_in.LoggedInScreen
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginScreen
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginScreenCoordinator
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginTransition
import com.tarasantoshchuk.rx_workflow.ui.ViewFactory

class AuthViewFactory : ViewFactory() {
    init {
        bindLayout(LoginScreen.KEY, LoginScreen.LAYOUT, LoginTransition()) { t ->
            LoginScreenCoordinator(t as LoginScreen)
        }
        bindLayout(ForgotPasswordScreen.KEY, ForgotPasswordScreen.LAYOUT, ForgotPasswordTransition()) { t ->
            ForgotPasswordScreenCoordinator(t as ForgotPasswordScreen)
        }
        bindLayout(AuthorizationScreen.KEY, AuthorizationScreen.LAYOUT) { t ->
            AuthorizationScreenCoordinator(t as AuthorizationScreen)
        }
        bindLayout(LoggedInScreen.KEY, LoggedInScreen.LAYOUT) { t ->
            LoggedInCoordinator(t as LoggedInScreen)
        }
    }
}