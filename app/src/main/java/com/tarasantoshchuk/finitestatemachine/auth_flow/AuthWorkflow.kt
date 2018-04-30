package com.tarasantoshchuk.finitestatemachine.auth_flow

import com.tarasantoshchuk.finitestatemachine.auth_flow.AuthWorkflow.AuthStateMachine.AuthStates
import com.tarasantoshchuk.finitestatemachine.auth_flow.AuthWorkflow.AuthStateMachine.AuthStates.*
import com.tarasantoshchuk.finitestatemachine.auth_flow.authorization.AuthorizationScreen
import com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password.ForgotPasswordScreen
import com.tarasantoshchuk.finitestatemachine.auth_flow.logged_in.LoggedInScreen
import com.tarasantoshchuk.finitestatemachine.auth_flow.login.LoginScreen
import com.tarasantoshchuk.finitestatemachine.commons.CommonScreenData
import com.tarasantoshchuk.rx_workflow.BaseWorkflow
import com.tarasantoshchuk.rx_workflow.CommonEvents
import com.tarasantoshchuk.rx_workflow.Event
import com.tarasantoshchuk.rx_workflow.finite_state_machine.FiniteStateMachine
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class AuthWorkflow : BaseWorkflow<Unit, AuthStates, Nothing>(AuthStateMachine()),
        LoginScreen.LoginEvents,
        ForgotPasswordScreen.Events, LoggedInScreen.Events {

    private val authBrick = AuthorizationBrick()

    override fun onSubmitEmail(email: String) {
        machine.accept(CommonEvents.BACK)
    }

    override fun onLogOut() {
        machine.accept(AuthStateMachine.AuthEvents.REQUEST_LOG_OUT)

        authBrick.logOut()
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        machine.accept(AuthStateMachine.AuthEvents.LOGGED_OUT)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        machine.accept(CommonEvents.FAIL)
                        commonData.onNext(CommonScreenData("Error logging OUT"))
                    }
                })
    }

    override fun state() = LOGIN

    override fun onLoginPressed(email: String, password: String) {
        machine.accept(AuthStateMachine.AuthEvents.SUBMIT_CREDENTIALS)

        authBrick.logIn(email, password)
                .subscribe (object : CompletableObserver {
                    override fun onComplete() {
                        machine.accept(AuthStateMachine.AuthEvents.AUTHORIZED)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        machine.accept(CommonEvents.FAIL)
                        commonData.onNext(CommonScreenData("Error logging IN"))
                    }
                })
    }

    override fun onResetPassword(email: String) {
        machine.accept(AuthStateMachine.AuthEvents.RESET_PASSWORD)
    }

    private val loginScreenMessage = BehaviorSubject.createDefault("hello")

    private val commonData = PublishSubject.create<CommonScreenData>()

    init {
        machine.apply {
            onState(LOGIN) {
                switchToScreen(LoginScreen(loginScreenMessage.map {
                    LoginScreen.LoginData(it)
                }, commonData, this@AuthWorkflow))
            }
            onState(AuthStates.FORGOT_PASSWORD) {
                switchToScreen(ForgotPasswordScreen(loginScreenMessage.map {
                    ForgotPasswordScreen.Data(it)
                }, commonData, this@AuthWorkflow))
            }
            onState(AuthStates.LOADING) {
                switchToScreen(AuthorizationScreen())
            }
            onState(AuthStates.SUCCESS) {
                switchToScreen(LoggedInScreen(authBrick.userData(), commonData, this@AuthWorkflow))
            }
        }
    }

    class AuthStateMachine : FiniteStateMachine<AuthStates>() {
        enum class AuthStates {
            LOGIN,
            LOADING,
            FORGOT_PASSWORD,
            SUCCESS
        }

        enum class AuthEvents : Event {
            SUBMIT_CREDENTIALS,
            RESET_PASSWORD,
            SUBMIT_EMAIL,
            EMAIL_SENT,
            AUTHORIZED,
            REQUEST_LOG_OUT,
            LOGGED_OUT
        }
        init {
            transition(LOGIN, AuthEvents.RESET_PASSWORD, FORGOT_PASSWORD)
            transition(LOGIN, AuthEvents.SUBMIT_CREDENTIALS, LOADING)
            transition(FORGOT_PASSWORD, AuthEvents.SUBMIT_EMAIL, LOADING)
            transition(FORGOT_PASSWORD, CommonEvents.BACK, LOGIN)
            transition(LOADING, AuthEvents.AUTHORIZED, SUCCESS)
            transitionBack(LOADING, CommonEvents.FAIL)
            transition(SUCCESS, AuthEvents.REQUEST_LOG_OUT, LOADING)
            transition(LOADING, AuthEvents.LOGGED_OUT, LOGIN)
        }
    }
}