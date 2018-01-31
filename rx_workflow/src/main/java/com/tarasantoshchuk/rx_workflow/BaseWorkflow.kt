package com.tarasantoshchuk.rx_workflow


import com.tarasantoshchuk.rx_workflow.util.FiniteStateMachine
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.MaybeSubject
import java.lang.RuntimeException

open class BaseWorkflow<in I, S: Any, R>(protected var machine: FiniteStateMachine<S>) : Workflow<I, R> {
    protected var result: MaybeSubject<R> = MaybeSubject.create()

    protected var screen: BehaviorSubject<WorkflowScreen<*, *>> = BehaviorSubject.create()

    override final fun start(input: I) {
        machine.start()
    }

    override final fun back() {
        machine.accept(CommonEvents.BACK)
    }

    override final fun abort() {
        result.onError(RuntimeException("aborted"))
    }

    override final fun result(): Maybe<R> {
        return result
    }

    override final fun screen(): Observable<WorkflowScreen<*, *>> {
        return screen
    }
}
