@file:Suppress("PackageName")

package com.tarasantoshchuk.rx_workflow.finite_state_machine

import com.tarasantoshchuk.rx_workflow.CommonEvents
import com.tarasantoshchuk.rx_workflow.Event
import java.util.*
import kotlin.reflect.KClass

open class FiniteStateMachine<S : Any> {
    private lateinit var state: S
    private val listener: CompositeListener<S> = CompositeListener()
    private val transitions: CompositeTransition<S> = CompositeTransition()

    private var backStack = Stack<S>()

    private var started = false

    constructor(): this({})

    constructor(initializer: FiniteStateMachine<S>.() -> Unit) {
        this.initializer()
        transitions.add(object : Transition<S> {
            override fun isApplicable(s: S, e: Event): Boolean {
                return e == CommonEvents.BACK
            }

            override fun apply(s: S, e: Event): S {
                return backStack.pop()
            }
        })

        onNewState { oldState: S, _: S ->
            backStack.push(oldState)
        }
    }

    fun startWith(initialState: S) {
        state = initialState
        started = true
        listener.onTransition(state, CommonEvents.START, state)
    }

    fun onAnyState(body: (S) -> Unit) {
        listener.add(object : Listener<S> {
            override fun onTransition(oldState: S, event: Event, newState: S) {
                body(newState)
            }
        })
    }

    private fun onNewState(body: (S, S) -> Unit) {
        listener.add(object : Listener<S> {
            override fun onTransition(oldState: S, event: Event, newState: S) {
                if (event != CommonEvents.BACK && event != CommonEvents.START) {
                    body(oldState, newState)
                }
            }
        })
    }

    fun onState(state: S, body: () -> Unit) {
        onAnyState {
            if (it == state) {
                body()
            }
        }
    }

    fun state() = state

    protected fun transition(state: S, event: Event, newState: S): MutableTransition<S> {
        return addAsMutableTransition(object : Transition<S> {
            override fun isApplicable(s: S, e: Event): Boolean {
                return e == event && s == state
            }

            override fun apply(s: S, e: Event): S {
                return newState
            }

        })
    }

    protected fun <TE : Event> transition(state: S, eventClass: KClass<TE>, body: (TE) -> S): MutableTransition<S> {
        val mutableTransition = MutableTransition(object : Transition<S> {
            override fun isApplicable(s: S, e: Event): Boolean {
                return eventClass == e::class && s == state
            }

            override fun apply(s: S, e: Event): S {
                //TODO::can we remove this warning?
                @Suppress("UNCHECKED_CAST")
                return body(e as TE)
            }
        })

        transitions.add(mutableTransition)

        return mutableTransition
    }

    private fun addAsMutableTransition(transition: Transition<S>): MutableTransition<S> {
        val mutableTransition = MutableTransition(transition)

        transitions.add(mutableTransition)

        return mutableTransition
    }

    fun accept(event: Event) {
        if (!started) {
            throw IllegalStateException("dispatching event to machine that is not yet started")
        }

        if (transitions.isApplicable(state, event)) {
            val newState = transitions.apply(state, event)

            listener.onTransition(state, event, newState)

            state = newState
        }
    }

    fun canGoBack(): Boolean {
        return !backStack.empty()
    }

    operator fun invoke(body: FiniteStateMachine<S>.() -> Unit) {
        this.body()
    }
}

interface Listener<in T : Any> {
    fun onTransition(oldState: T, event: Event, newState: T)
}

class CompositeListener<T : Any> : Listener<T> {
    private val listeners: MutableList<Listener<T>> = ArrayList()

    override fun onTransition(oldState: T, event: Event, newState: T) {
        for (listener in listeners) {
            listener.onTransition(oldState, event, newState)
        }
    }

    fun add(listener: Listener<T>) {
        listeners.add(listener)
    }
}

