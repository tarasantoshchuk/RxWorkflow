package com.tarasantoshchuk.finitestatemachine.commons

import com.tarasantoshchuk.rx_workflow.ui.WorkflowScreen
import io.reactivex.Observable


abstract class BaseWorkflowScreen<D, E>(
        key: String,
        screenData: Observable<D>,
        val commonData: Observable<CommonScreenData>,
        eventHandler: E): WorkflowScreen<D, E>(key, screenData, eventHandler) {
}