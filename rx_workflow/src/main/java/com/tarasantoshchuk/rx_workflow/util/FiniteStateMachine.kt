package com.tarasantoshchuk.rx_workflow.util

import com.tarasantoshchuk.rx_workflow.finite_state_machine.FiniteStateMachine
import com.tarasantoshchuk.rx_workflow.Event

open class FiniteStateMachine<S: Any>(initialState: S) : FiniteStateMachine<S, Event>(initialState)
