package com.tarasantoshchuk.rx_workflow.util

import com.tarasantoshchuk.rx_workflow.Event
import com.tarasantoshchuk.rx_workflow.finite_state_machine.FiniteStateMachine

open class FiniteStateMachine<S: Any> : FiniteStateMachine<S, Event>()
