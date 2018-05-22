package com.tarasantoshchuk.rx_workflow.impl


import android.support.annotation.CallSuper
import com.tarasantoshchuk.rx_workflow.core.TerminationKey
import com.tarasantoshchuk.rx_workflow.core.Workflow
import com.tarasantoshchuk.rx_workflow.fsm.FiniteStateMachine
import com.tarasantoshchuk.rx_workflow.ui.WorkflowScreen
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.SingleSubject

abstract class BaseWorkflow<S: Any>(protected var machine: FiniteStateMachine<S>) : Workflow {
    private var terminationKey: SingleSubject<TerminationKey> = SingleSubject.create()
    private var screen: BehaviorSubject<WorkflowScreen<*, *>> = BehaviorSubject.create()

    protected fun switchToScreen(nextScreen: WorkflowScreen<*, *>) {
        screen.onNext(nextScreen)
    }

    @CallSuper
    override fun start() {
        machine.attachToWorkflow(this)
        machine.startWith(initialState())
    }

    protected abstract fun initialState(): S

    override fun back(): Boolean {
        return machine.back()
    }

    final override fun doFinish(key: TerminationKey) {
        terminationKey.onSuccess(key)
    }

    final override fun finish(): Single<TerminationKey> {
        return terminationKey
    }

    final override fun screen(): Observable<WorkflowScreen<*, *>> {
        return screen
    }
}
