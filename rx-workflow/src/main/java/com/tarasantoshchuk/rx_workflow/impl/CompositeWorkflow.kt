package com.tarasantoshchuk.rx_workflow.impl

import com.tarasantoshchuk.rx_workflow.impl.CompositeWorkflow.CompositeMachine.States
import com.tarasantoshchuk.rx_workflow.core.Event
import com.tarasantoshchuk.rx_workflow.core.TerminationKey
import com.tarasantoshchuk.rx_workflow.core.Workflow
import com.tarasantoshchuk.rx_workflow.fsm.FiniteStateMachine

class CompositeWorkflow(private var initialWorkflow: Workflow, vararg workflowBindings: WorkflowBinding) : BaseWorkflow<States>(CompositeMachine(workflowBindings)) {
    override fun initialState(): States {
        return States(initialWorkflow)
    }

    class CompositeMachine(workflowBindings: Array<out WorkflowBinding>) : FiniteStateMachine<States>() {
        class States(var workflow: Workflow)

        class Events constructor(val newWorkflow: Workflow): Event

        private lateinit var currentWorkflow: Workflow

        init {
            onAnyState {
                currentWorkflow = it.workflow

                currentWorkflow.start()

                currentWorkflow.screen()
                        .subscribe {
                            (workflow as CompositeWorkflow).switchToScreen(it)
                        }

                currentWorkflow.finish()
                        .doOnSuccess { key ->
                            for (binding in workflowBindings) {
                                if (binding.fromWorkflow.isInstance(currentWorkflow) && binding.terminationKey === key) {
                                    this@CompositeMachine.accept(Events(binding.toWorkflow.newInstance()))
                                }
                            }
                        }
                        .subscribe()
            }

            universalTransition<Events> {
                States(it.newWorkflow)
            }
        }

        override fun back(): Boolean {
            return currentWorkflow.back()
        }
    }
}

class WorkflowBinding constructor(
        val fromWorkflow: Class<out Workflow>,
        val terminationKey: TerminationKey,
        val toWorkflow: Class<out Workflow>)


