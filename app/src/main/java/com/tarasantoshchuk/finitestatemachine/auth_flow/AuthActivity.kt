package com.tarasantoshchuk.finitestatemachine.auth_flow

import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.rx_workflow.core.TerminationKey
import com.tarasantoshchuk.rx_workflow.core.WorkflowActivity
import com.tarasantoshchuk.rx_workflow.core.Workflows
import com.tarasantoshchuk.rx_workflow.impl.CompositeWorkflow
import com.tarasantoshchuk.rx_workflow.impl.WorkflowBinding

class AuthActivity : WorkflowActivity() {
    override fun createWorkflows() = Workflows {
        registerWorkflow(
                CompositeWorkflow(AuthWorkflow(), WorkflowBinding(AuthWorkflow::class.java, TerminationKey.FINISH, AuthWorkflow::class.java)),
                AuthViewFactory()
        ) {
            it.findViewById(R.id.root1)
        }
        registerWorkflow(
                CompositeWorkflow(AuthWorkflow(), WorkflowBinding(AuthWorkflow::class.java, TerminationKey.FINISH, AuthWorkflow::class.java)),
                AuthViewFactory()
        ) {
            it.findViewById(R.id.root2)
        }
    }

    override fun setContentView() {
        setContentView(R.layout.activity_main)
    }
}

