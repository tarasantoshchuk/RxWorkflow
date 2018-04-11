package com.tarasantoshchuk.finitestatemachine.auth_flow

import android.view.ViewGroup
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.auth_flow.AuthStateMachine.AuthEvents.*
import com.tarasantoshchuk.finitestatemachine.auth_flow.AuthStateMachine.AuthStates
import com.tarasantoshchuk.finitestatemachine.auth_flow.AuthStateMachine.AuthStates.*
import com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password.ForgotPasswordScreen
import com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password.ForgotPasswordScreenCoordinator
import com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password.ForgotPasswordTransition
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginScreen
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginScreenCoordinator
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginTransition
import com.tarasantoshchuk.rx_workflow.*
import com.tarasantoshchuk.rx_workflow.finite_state_machine.FiniteStateMachine
import io.reactivex.subjects.BehaviorSubject

class MainActivity : com.tarasantoshchuk.rx_workflow.WorkflowActivity<Unit, Nothing>() {
    override fun getRootView(): ViewGroup {
        return findViewById(R.id.root)
    }

    override fun createWorkflow(): Workflow<Unit, Nothing> {
        return AuthWorkflow()
    }

    override fun createViewFactory(): ViewFactory {
        return AuthViewFactory()
    }

    override fun setContentView() {
        setContentView(R.layout.activity_main)
    }
}

class AuthViewFactory : ViewFactory() {
    init {
        bindLayout(LoginScreen.KEY, LoginScreen.LAYOUT, LoginTransition()) { t ->
            LoginScreenCoordinator(t as LoginScreen)
        }
        bindLayout(ForgotPasswordScreen.KEY, ForgotPasswordScreen.LAYOUT, ForgotPasswordTransition()) { t ->
            ForgotPasswordScreenCoordinator(t as ForgotPasswordScreen)
        }
    }
}

class AuthWorkflow : BaseWorkflow<Unit, AuthStates, Nothing>(AuthStateMachine()),
        LoginScreen.LoginEvents,
        ForgotPasswordScreen.Events
{
    override fun onSubmitEmail(email: String) {
        machine.accept(CommonEvents.BACK)
    }

    override fun state() = LOGIN

    override fun onLoginPressed(email: String, password: String) {
        loginScreenMessage.onNext("pressed")
    }

    override fun onResetPassword(email: String) {
        machine.accept(RESET_PASSWORD)
    }

    private val loginScreenMessage = BehaviorSubject.createDefault("hello")

    init {
        machine.apply {
            onState(LOGIN) {
                switchToScreen(LoginScreen(loginScreenMessage.map {
                    LoginScreen.LoginData(it)
                }, this@AuthWorkflow))
            }
            onState(FORGOT_PASSWORD) {
                switchToScreen(ForgotPasswordScreen(loginScreenMessage.map {
                    ForgotPasswordScreen.Data(it)
                }, this@AuthWorkflow))
            }
        }
    }
}

class AuthStateMachine : FiniteStateMachine<AuthStates>() {
    enum class AuthStates {
        LOGIN,
        AUTHORIZATION,
        FORGOT_PASSWORD,
        SENDING_EMAIL,
        SUCCESS
    }

    enum class AuthEvents : Event {
        SUBMIT_CREDENTIALS,
        RESET_PASSWORD,
        SUBMIT_EMAIL,
        EMAIL_SENT,
        AUTHORIZED,
        AUTHORIZATION_FAILED
    }
    init {
        transition(LOGIN, AuthEvents.RESET_PASSWORD, FORGOT_PASSWORD)
//        transition(FORGOT_PASSWORD, AuthEvents.SUBMIT_EMAIL, SENDING_EMAIL)
//        transition(LOGIN, SUBMIT_CREDENTIALS, AuthStates.AUTHORIZATION)
//        transition(SENDING_EMAIL, AuthEvents.EMAIL_SENT, LOGIN)
//        transition(AUTHORIZATION, AUTHORIZED, SUCCESS)
//        transition(AUTHORIZATION, AUTHORIZATION_FAILED, LOGIN)
    }
}

