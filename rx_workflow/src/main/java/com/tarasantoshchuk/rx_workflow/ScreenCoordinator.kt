package com.tarasantoshchuk.rx_workflow


import android.view.View

import com.squareup.coordinators.Coordinator

import io.reactivex.Observable

abstract class ScreenCoordinator<WS : WorkflowScreen<D, E>, out E, D>(private val mWorkflowScreen: WS) : Coordinator() {

    abstract override fun attach(view: View?)

    override fun detach(view: View?) {

    }

    protected fun eventsHandler(): E {
        return mWorkflowScreen.eventHandler
    }

    protected fun screenData(): Observable<D> {
        return mWorkflowScreen.screenData
    }
}

