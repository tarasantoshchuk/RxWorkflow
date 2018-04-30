package com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password

import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.utils.textString
import com.tarasantoshchuk.rx_workflow.butterknife.ButterknifeScreenCoordinator


class ForgotPasswordScreenCoordinator(mWorkflowScreen: ForgotPasswordScreen) :
        ButterknifeScreenCoordinator<ForgotPasswordScreen, ForgotPasswordScreen.Events, ForgotPasswordScreen.Data>(mWorkflowScreen) {
    @BindView(R.id.et_email)
    lateinit var email: TextView

    @OnClick(R.id.reset_password)
    fun onResetClick() {
        eventsHandler().onSubmitEmail(email.textString())
    }
}
