package com.tarasantoshchuk.rx_workflow.core

import com.tarasantoshchuk.rx_workflow.ui.ViewFactory
import com.tarasantoshchuk.rx_workflow.ui.WorkflowScreen
import io.reactivex.Observable
import io.reactivex.Single


interface Workflow {
    fun start()

    fun back(): Boolean

    fun doFinish(key: TerminationKey)

    fun finish(): Single<TerminationKey>

    fun screen(): Observable<WorkflowScreen<*, *>>
}

data class TerminationKey(val key: String) {
    companion object {
        val BACK = TerminationKey("back")
        val FINISH = TerminationKey("finish")
    }
}
