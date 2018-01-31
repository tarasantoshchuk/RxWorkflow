package com.tarasantoshchuk.rx_workflow

import io.reactivex.Observable

abstract class WorkflowScreen<D, out E> protected constructor(
        val key: String,
        val screenData: Observable<D>,
        val eventHandler: E
)
