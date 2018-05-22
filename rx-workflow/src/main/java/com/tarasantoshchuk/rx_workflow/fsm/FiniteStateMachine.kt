package com.tarasantoshchuk.rx_workflow.fsm

import android.util.Log
import com.tarasantoshchuk.rx_workflow.impl.CommonEvents
import com.tarasantoshchuk.rx_workflow.core.Event
import com.tarasantoshchuk.rx_workflow.core.Workflow
import java.util.*
import kotlin.reflect.KClass

open class FiniteStateMachine<S : Any> {
    private lateinit var state: S
    private val listener: CompositeListener<S> = CompositeListener()
    private val transitions: CompositeTransition<S> = CompositeTransition()

    private var backStack = Stack<S>()

    private var started = false
    private var halted = false

    protected lateinit var workflow: Workflow

    constructor(): this({})

    constructor(initializer: FiniteStateMachine<S>.() -> Unit) {
        this.initializer()

        onNewState { oldState: S, _: S ->
            backStack.push(oldState)
        }
    }

    fun startWith(initialState: S) {
        if (started) {
            throw IllegalStateException("already started")
        }

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
                if (event.isNewState()) {
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

    protected inline fun <reified TE> universalTransition(crossinline newState: (TE) -> S): MutableTransition<S> {
        return addAsMutableTransition(object: Transition<S> {
            override fun isApplicable(s: S, e: Event): Boolean {
                return e is TE
            }

            override fun apply(s: S, e: Event): S {
                return newState(e as TE)
            }
        })
    }

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

    protected fun terminalTransition(statePredicate: (S) -> Boolean, event: Event): MutableTransition<S> {
        return addAsMutableTransition(object : Transition<S> {
            override fun isApplicable(s: S, e: Event): Boolean {
                return e == event && statePredicate(s)
            }

            override fun apply(s: S, e: Event): S {
                halt()
                return s
            }
        })
    }

    private fun halt() {
        halted = true
    }

    protected fun transitionBack(state: S, event: Event): MutableTransition<S> {
        return addAsMutableTransition(object : Transition<S> {
            override fun isApplicable(s: S, e: Event): Boolean {
                return e == event && s == state
            }

            override fun apply(s: S, e: Event): S {
                return backStack.pop()
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

    protected fun addAsMutableTransition(transition: Transition<S>): MutableTransition<S> {
        val mutableTransition = MutableTransition(transition)

        transitions.add(mutableTransition)

        return mutableTransition
    }

    fun accept(event: Event): Boolean {
        if (!started || halted) {
            throw IllegalStateException("dispatching event to machine that is not yet started")
        }

        Log.v("AEROL", "onPreAccept: $this: started $started, halted $halted, event $event, state $state")
        Log.v("AEROL", "backStack: $this: $backStack")

        if (transitions.isApplicable(state, event)) {
            val newState = transitions.apply(state, event)

            if (!halted) {
                listener.onTransition(state, event, newState)

                state = newState
            }

            Log.v("AEROL", "onPostAccept: $this: started $started, halted $halted, event $event, state $state")
            Log.v("AEROL", "backStack: $this: $backStack")

            return true
        }

        return false
    }

    operator fun invoke(body: FiniteStateMachine<S>.() -> Unit) {
        this.body()
    }

    open fun back(): Boolean {
        return accept(CommonEvents.BACK)
    }

    fun attachToWorkflow(baseWorkflow: Workflow) {
        workflow = baseWorkflow
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

