package com.tarasantoshchuk.rx_workflow.ui


import android.view.View

import com.squareup.coordinators.Coordinator

import io.reactivex.Observable

abstract class ScreenCoordinator<WS : WorkflowScreen<D, E>, out E, D>(private val workflowScreen: WS) : Coordinator() {

    abstract override fun attach(view: View)

    override fun detach(view: View) {
    }

    protected fun eventsHandler(): E {
        return workflowScreen.eventHandler
    }

    protected fun screenData(): Observable<D> {
        return workflowScreen.screenData
    }
}

