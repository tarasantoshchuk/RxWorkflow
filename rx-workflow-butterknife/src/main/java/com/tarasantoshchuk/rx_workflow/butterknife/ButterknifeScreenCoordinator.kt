package com.tarasantoshchuk.rx_workflow.butterknife

import android.view.View
import butterknife.ButterKnife
import butterknife.Unbinder
import com.tarasantoshchuk.rx_workflow.ScreenCoordinator
import com.tarasantoshchuk.rx_workflow.WorkflowScreen


abstract class ButterknifeScreenCoordinator<WS : WorkflowScreen<D, E>, out E, D>(workflowScreen: WS): ScreenCoordinator<WS, E, D>(workflowScreen) {
    private lateinit var unbinder: Unbinder

    final override fun attach(view: View) {
        unbinder = ButterKnife.bind(this, view)

        attach()
    }

    protected open fun attach() {}

    final override fun detach(view: View) {
        detach()

        unbinder.unbind()
    }

    protected fun detach() {}
}