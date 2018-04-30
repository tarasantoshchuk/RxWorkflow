package com.tarasantoshchuk.finitestatemachine.auth_flow.forgot_password

import android.transition.ChangeBounds
import android.transition.Fade
import android.transition.TransitionSet
import android.view.View
import butterknife.BindView
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.rx_workflow.butterknife.ButterKnifeTransitionBuilder

class ForgotPasswordTransition : ButterKnifeTransitionBuilder() {
    @BindView(R.id.content)
    lateinit var content: View

    override fun doContributeEnterTransition(transition: TransitionSet, view: View) {
        transition.addTransition(Fade().addTarget(content).setDuration(1000))
    }

    override fun doContributeExitTransition(transition: TransitionSet, view: View) {
        transition.addTransition(Fade().addTarget(content).setDuration(1000))
        transition.addTransition(ChangeBounds().addTarget(R.id.reset_password).setDuration(1000))
    }
}
