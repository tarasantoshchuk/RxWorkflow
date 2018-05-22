package com.tarasantoshchuk.rx_workflow.ui

import io.reactivex.Observable

abstract class WorkflowScreen<D, out E> protected constructor(
        val key: String,
        val screenData: Observable<D>,
        val eventHandler: E
)
