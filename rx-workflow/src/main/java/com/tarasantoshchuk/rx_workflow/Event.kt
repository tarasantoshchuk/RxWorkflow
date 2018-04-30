package com.tarasantoshchuk.rx_workflow


interface Event {
    fun isNewState() = true
}
