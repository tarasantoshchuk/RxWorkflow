package com.tarasantoshchuk.rx_workflow.butterknife

import android.support.annotation.CallSuper
import android.view.View
import butterknife.ButterKnife
import butterknife.Unbinder
import com.tarasantoshchuk.rx_workflow.ui.ScreenCoordinator
import com.tarasantoshchuk.rx_workflow.ui.WorkflowScreen


abstract class ButterknifeScreenCoordinator<WS : WorkflowScreen<D, E>, out E, D>(workflowScreen: WS): ScreenCoordinator<WS, E, D>(workflowScreen) {
    private lateinit var unbinder: Unbinder

    @CallSuper
    override fun attach(view: View) {
        unbinder = ButterKnife.bind(this, view)
    }

    @CallSuper
    override fun detach(view: View) {
        unbinder.unbind()
    }
}