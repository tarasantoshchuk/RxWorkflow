package com.tarasantoshchuk.finitestatemachine.auth_flow

import android.view.ViewGroup
import com.tarasantoshchuk.rx_workflow.impl.CompositeWorkflow
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.rx_workflow.impl.WorkflowBinding
import com.tarasantoshchuk.rx_workflow.core.TerminationKey
import com.tarasantoshchuk.rx_workflow.core.Workflow
import com.tarasantoshchuk.rx_workflow.core.WorkflowActivity
import com.tarasantoshchuk.rx_workflow.ui.ViewFactory

class AuthActivity : WorkflowActivity() {
    override fun getRootView(): ViewGroup {
        return findViewById(R.id.root)
    }

    override fun createWorkflow(): Workflow {
        return CompositeWorkflow(AuthWorkflow(),
                WorkflowBinding(AuthWorkflow::class.java, TerminationKey.FINISH, AuthWorkflow::class.java))
    }

    override fun createViewFactory(): ViewFactory {
        return AuthViewFactory()
    }

    override fun setContentView() {
        setContentView(R.layout.activity_main)
    }
}

