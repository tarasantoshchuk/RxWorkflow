package com.tarasantoshchuk.rx_workflow.impl

import com.tarasantoshchuk.rx_workflow.core.Event

enum class CommonEvents : Event {
    START,
    BACK,
    FAIL;

    override fun isNewState() = false
}