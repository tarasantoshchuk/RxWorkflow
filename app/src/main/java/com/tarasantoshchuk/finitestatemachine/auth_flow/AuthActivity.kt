package com.tarasantoshchuk.finitestatemachine.auth_flow

import android.view.ViewGroup
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.rx_workflow.*

class AuthActivity : com.tarasantoshchuk.rx_workflow.WorkflowActivity<Unit, Nothing>() {
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

