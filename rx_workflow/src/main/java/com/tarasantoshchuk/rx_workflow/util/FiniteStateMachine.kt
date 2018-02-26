package com.tarasantoshchuk.rx_workflow.util

import com.tarasantoshchuk.rx_workflow.Event
import com.tarasantoshchuk.rx_workflow.finite_state_machine.FiniteStateMachine as FiniteStateMachineBasic

open class FiniteStateMachine<S : Any> : FiniteStateMachineBasic<S, Event> {
    constructor() : super()
    constructor(initializer: FiniteStateMachineBasic<S, Event>.() -> Unit) : super(initializer)
}

operator fun <S : Any> FiniteStateMachine<S>.invoke(body: FiniteStateMachine<S>.() -> Unit) {
    this.body()
}