package com.tarasantoshchuk.rx_workflow

import io.reactivex.Maybe
import io.reactivex.Observable


interface Workflow<in I, R> {
    fun start(input:I)

    fun back()

    fun abort()

    fun result(): Maybe<R>

    fun screen(): Observable<WorkflowScreen<*, *>>
}