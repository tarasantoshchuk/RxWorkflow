package com.tarasantoshchuk.finitestatemachine

import com.tarasantoshchuk.finitestatemachine.AuthEvents.*
import com.tarasantoshchuk.finitestatemachine.AuthStates.*
import com.tarasantoshchuk.rx_workflow.BaseWorkflow
import com.tarasantoshchuk.rx_workflow.Event
import com.tarasantoshchuk.rx_workflow.ViewFactory
import com.tarasantoshchuk.rx_workflow.Workflow
import com.tarasantoshchuk.rx_workflow.util.FiniteStateMachine
import io.reactivex.subjects.BehaviorSubject

class MainActivity : com.tarasantoshchuk.rx_workflow.WorkflowActivity<Unit, Nothing>() {
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
        bindLayout(LoginScreen.KEY, R.layout.login) { t ->
            LoginScreenCoordinator(t as LoginScreen)
        }
    }
}

class AuthWorkflow : BaseWorkflow<Unit, AuthStates, Nothing>(AuthStateMachine()),
        LoginScreen.LoginEvents {
    override fun state() = AuthStates.LOGIN

    override fun onLoginPressed(email: String, password: String) {
        loginScreenMessage.onNext("pressed")
    }

    override fun onResetPassword(email: String) {
        loginScreenMessage.onNext("forgot password??")
    }

    private val loginScreenMessage = BehaviorSubject.createDefault("hello")

    init {
        machine.apply {
            onState(LOGIN) {
                screen.onNext(LoginScreen(loginScreenMessage.map {
                    LoginScreen.LoginData(it)
                }, this@AuthWorkflow))
            }
        }
    }
}

class AuthStateMachine : FiniteStateMachine<AuthStates>() {
    init {
        transition(LOGIN, SUBMIT_CREDENTIALS, AUTHORIZATION)
        transition(LOGIN, RESET_PASSWORD, FORGOT_PASSWORD)
        transition(FORGOT_PASSWORD, SUBMIT_EMAIL, SENDING_EMAIL)
        transition(SENDING_EMAIL, EMAIL_SENT, LOGIN)
        transition(AUTHORIZATION, AUTHORIZED, SUCCESS)
        transition(AUTHORIZATION, AUTHORIZATION_FAILED, LOGIN)
    }
}

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
