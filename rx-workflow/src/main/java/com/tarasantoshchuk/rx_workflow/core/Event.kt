package com.tarasantoshchuk.rx_workflow.core


interface Event {
    fun isNewState() = true
}
