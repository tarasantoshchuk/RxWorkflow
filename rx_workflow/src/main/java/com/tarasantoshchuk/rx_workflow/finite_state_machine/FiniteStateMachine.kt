package com.tarasantoshchuk.rx_workflow.finite_state_machine

import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

open class FiniteStateMachine<S: Any, E: Any> {
    private lateinit var state: S
    private val listener: CompositeListener<S> = CompositeListener()
    private val transitions: CompositeTransition<S, E> = CompositeTransition()

    private val acceptEntryWatch: AtomicBoolean = AtomicBoolean()

    private var started = false

    fun startWith(initialState: S) {
        state = initialState
        started = true
    }

    fun onEntry(l:(S) -> Unit) {
        listener.add(object: Listener<S> {
            override fun onTransition(newState: S) {
                l(newState)
            }
        })

        if (started) {
            l(state)
        }
    }

    fun onEntry(state: S, l:() -> Unit) {
        onEntry {
            if (it == state) {
                l()
            }
        }
    }

    protected fun onEvent(e: E, transition:(S) -> S) {
        transitions.add(object: Transition<S, E> {
            override fun isApplicable(oldState: S, event: E) = event == e

            override fun apply(oldState: S, event: E): S {
                if (isApplicable(oldState, event))
                    return transition(oldState)

                throw IllegalArgumentException()
            }

        })
    }

    protected fun transition(oldState: S, event: E, newState: S): ModifiableTransition {

    }

    fun accept(event: E) {
        if (!started) {
            throw IllegalStateException("dispatching event to machine that is not yet started")
        }

        if (!acceptEntryWatch.compareAndSet(false, true)) {
            throw IllegalAccessException("trying to dispatch event from withing transition listener")
        }

        if (transitions.isApplicable(state, event)) {
            val newState = transitions.apply(state, event)

            listener.onTransition(newState)

            state = newState
        }

        acceptEntryWatch.set(false)
    }

}

interface Listener<in T: Any> {
    fun onTransition(newState: T)
}

class CompositeListener<in T: Any> : Listener<T> {
    private val listeners: MutableList<Listener<T>> = ArrayList()

    override fun onTransition(newState: T) {
        for (listener in listeners) {
            listener.onTransition(newState)
        }
    }

    fun add(listener: Listener<T>) {
        listeners.add(listener)
    }
}

interface Transition<S: Any, in E: Any> {
    fun isApplicable(oldState: S, event:E): Boolean
    fun apply(oldState: S, event:E): S
}

class ModifiableTransition<S: Any, in E: Any>(val inner: Transition<S, E>) : Transition<S, E> {
    override fun apply(oldState: S, event: E): S {

    }

    override fun isApplicable(oldState: S, event: E): Boolean {

    }

    fun onlyIf(event: E) {

    }
}

class CompositeTransition<S: Any, E: Any> : Transition<S, E> {
    private val transitions: MutableList<Transition<S, E>> = ArrayList()

    override fun isApplicable(oldState: S, event: E): Boolean {
        for (transition in transitions) {
            if (transition.isApplicable(oldState, event)) {
                return true
            }
        }

        return false
    }

    override fun apply(oldState: S, event: E): S {
        for (transition in transitions) {
            if (transition.isApplicable(oldState, event)) {
                return transition.apply(oldState, event)
            }
        }

        throw NoSuchElementException()
    }

    fun add(t: Transition<S, E>) {
        transitions.add(t)
    }
}
