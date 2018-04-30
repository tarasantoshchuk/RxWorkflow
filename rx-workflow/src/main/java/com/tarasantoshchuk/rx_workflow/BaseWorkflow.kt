package com.tarasantoshchuk.rx_workflow


import com.tarasantoshchuk.rx_workflow.finite_state_machine.FiniteStateMachine
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.MaybeSubject
import java.lang.RuntimeException

abstract class BaseWorkflow<in I, S: Any, R>(protected var machine: FiniteStateMachine<S>) : Workflow<I, R> {

    private var result: MaybeSubject<R> = MaybeSubject.create()

    private var screen: BehaviorSubject<WorkflowScreen<*, *>> = BehaviorSubject.create()

    protected fun switchToScreen(nextScreen: WorkflowScreen<*, *>) {
        screen.onNext(nextScreen)
    }

    final override fun start(input: I) {
        machine.startWith(state())
    }

    protected abstract fun state(): S

    final override fun back(): Boolean {
        return machine.accept(CommonEvents.BACK)
    }

    final override fun abort() {
        result.onError(RuntimeException("aborted"))
    }

    final override fun result(): Maybe<R> {
        return result
    }

    final override fun screen(): Observable<WorkflowScreen<*, *>> {
        return screen
    }
}
