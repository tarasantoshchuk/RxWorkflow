package com.tarasantoshchuk.rx_workflow.finite_state_machine


open class CompositeStateMachine<T: Any>(initialState: FiniteStateMachine<*, T>) : FiniteStateMachine<FiniteStateMachine<*, T>, FiniteStateMachine<*, T>>(initialState) {
    val subMachinesListener: CompositeSubmachineListener<T> = CompositeSubmachineListener()

    init {
        onEntry {
            attachSubmachineListener(it)
        }

        onAnyEvent { _, event: FiniteStateMachine<*, T> ->
            event
        }
    }

    private fun attachSubmachineListener(newState: FiniteStateMachine<*, T>) {
        newState.onEntry({
            subMachinesListener.onState(newState, it)
        })
    }

    inline fun <reified FSM: FiniteStateMachine<*, T>> bindStates(switchMachine: FiniteStateMachine<*, T>, terminalState: T) {
        bindStates<FSM>(switchMachine) {
            it == terminalState
        }
    }

    inline fun <reified FSM1: FiniteStateMachine<*, T>> bindStates(switchMachine: FiniteStateMachine<*, T>, crossinline terminalState: (Any) -> Boolean) {
        subMachinesListener.add(object: SubmachineListener<T> {
            override fun onState(machine: FiniteStateMachine<*, T>, newState: Any) {
                if (machine is FSM1 && terminalState(newState)) {
                    accept(switchMachine)
                }
            }
        })
    }
}

class MathMachine: CompositeStateMachine<Int>(AddMachine(0)) {
    init {
        bindStates<AddMachine>(SubtractsMachine(100)) {
            it as Int >= 100
        }

        bindStates<SubtractsMachine>(AddMachine(0)) {
            it as Int <= 0
        }
    }
}



interface SubmachineListener<T: Any> {
    fun onState(machine: FiniteStateMachine<*, T>, newState: Any)
}

class CompositeSubmachineListener<T: Any>: SubmachineListener<T> {
    override fun onState(machine: FiniteStateMachine<*, T>, newState: Any) {
        for (l in listeners) {
            l.onState(machine, newState)
        }
    }

    private val listeners: MutableList<SubmachineListener<T>> = ArrayList()

    fun add(l: SubmachineListener<T>) {
        listeners.add(l)
    }
}