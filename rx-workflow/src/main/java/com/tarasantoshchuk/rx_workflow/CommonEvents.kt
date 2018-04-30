package com.tarasantoshchuk.rx_workflow

enum class CommonEvents : Event {
    START,
    BACK,
    FAIL;

    override fun isNewState() = false
}