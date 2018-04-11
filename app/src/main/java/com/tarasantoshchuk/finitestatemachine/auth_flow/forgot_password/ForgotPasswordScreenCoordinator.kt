package com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password

import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.utils.textString
import com.tarasantoshchuk.rx_workflow.butterknife.ButterknifeScreenCoordinator


class ForgotPasswordScreenCoordinator(mWorkflowScreen: ForgotPasswordScreen) :
        ButterknifeScreenCoordinator<ForgotPasswordScreen, ForgotPasswordScreen.Events, ForgotPasswordScreen.Data>(mWorkflowScreen) {
    lateinit var unbinder: Unbinder

    @BindView(R.id.et_email)
    lateinit var email: TextView

    @OnClick(R.id.reset_password)
    fun onResetClick() {
        eventsHandler().onSubmitEmail(email.textString())
    }
}
