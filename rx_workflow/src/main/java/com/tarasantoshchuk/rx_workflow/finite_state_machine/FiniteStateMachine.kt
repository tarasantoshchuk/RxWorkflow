@file:Suppress("PackageName")

package com.tarasantoshchuk.rx_workflow.finite_state_machine

import java.util.*
import kotlin.NoSuchElementException
import kotlin.reflect.KClass

open class FiniteStateMachine<S: Any, E: Any> {
    private lateinit var state: S
    private val listener: CompositeListener<S> = CompositeListener()
    private val transitions: CompositeTransition<S, E> = CompositeTransition()

    private var started = false

    constructor()

    @Suppress("unused")
    constructor(initializer: FiniteStateMachine<S, E>.() -> Unit) {
        this.initializer()
    }

    @Suppress("unused")
    fun startWith(initialState: S) {
        state = initialState
        started = true
    }

    @Suppress("MemberVisibilityCanPrivate")
    fun onAnyState(body:(S) -> Unit) {
        listener.add(object: Listener<S> {
            override fun onTransition(newState: S) {
                body(newState)
            }
        })

        if (started) {
            body(state)
        }
    }

    @Suppress("unused")
    fun onState(state: S, body:() -> Unit) {
        onAnyState {
            if (it == state) {
                body()
            }
        }
    }

    @Suppress("unused")
    protected fun transition(state: S, event: E, newState: S): MutableTransition<S, E> {
        return addAsMutableTransition(object : Transition<S, E> {
            override fun isApplicable(s: S, e: E): Boolean {
                return e == event && s == state
            }

            override fun apply(s: S, e: E): S {
                return newState
            }

        })
    }

    @Suppress("unused")
    protected fun <TE: E> transition(state: S, eventClass: KClass<TE>, body: (TE) -> S): MutableTransition<S, E> {
        val mutableTransition = MutableTransition(object : Transition<S, E> {
            override fun isApplicable(s: S, e: E): Boolean {
                return eventClass == e::class && s == state
            }

            override fun apply(s: S, e: E): S {
                //TODO::can we remove this warning?
                @Suppress("UNCHECKED_CAST")
                return body(e as TE)
            }
        })

        transitions.add(mutableTransition)

        return mutableTransition
    }

    private fun addAsMutableTransition(transition: Transition<S, E>): MutableTransition<S, E> {
        val mutableTransition = MutableTransition(transition)

        transitions.add(mutableTransition)

        return mutableTransition
    }

    fun accept(event: E) {
        if (!started) {
            throw IllegalStateException("dispatching event to machine that is not yet started")
        }

        if (transitions.isApplicable(state, event)) {
            val newState = transitions.apply(state, event)

            listener.onTransition(newState)

            state = newState
        }
    }

}

interface Listener<in T: Any> {
    fun onTransition(newState: T)
}

class CompositeListener<T: Any> : Listener<T> {
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
    fun isApplicable(s: S, e: E): Boolean
    fun apply(s: S, e:E): S

    fun applyIfApplicable(s: S, e: E): Pair<Boolean, S> {
        if (isApplicable(s, e)) {
            return Pair(true, apply(s, e))
        }

        return Pair(false, s)
    }
}

interface EditableTransition<S: Any, E: Any>: Transition<S, E> {
    fun editIsApplicable(applicableAction: (S, E, Boolean) -> Boolean): EditableTransition<S, E>
    fun editApply(applyAction: (S, E, S) -> S): EditableTransition<S, E>
}

open class SimpleEditableTransition<S: Any, E: Any>(private val inner: Transition<S, E>) : EditableTransition<S, E> {
    override fun isApplicable(s: S, e: E) = inner.isApplicable(s, e)

    override fun apply(s: S, e: E) = inner.apply(s, e)

    override fun editApply(applyAction: (S, E, S) -> S): EditableTransition<S, E> =
        object: SimpleEditableTransition<S, E>(this) {
            override fun apply(s: S, e: E): S {
                return applyAction(s, e, apply(s, e))
            }
        }

    override fun editIsApplicable(applicableAction: (S, E, Boolean) -> Boolean): EditableTransition<S, E> =
            object: SimpleEditableTransition<S, E>(this) {
                override fun isApplicable(s: S, e: E): Boolean {
                    return applicableAction(s, e, isApplicable(s, e))
                }
            }
}

class MutableTransition<S: Any, E: Any>(transition: Transition<S, E>) : EditableTransition<S, E> {
    private var inner: EditableTransition<S, E> = SimpleEditableTransition(transition)

    override fun editIsApplicable(applicableAction: (S, E, Boolean) -> Boolean): EditableTransition<S, E> {
        inner = inner.editIsApplicable(applicableAction)
        return inner
    }

    override fun editApply(applyAction: (S, E, S) -> S): EditableTransition<S, E> {
        inner = inner.editApply(applyAction)
        return inner
    }

    override fun isApplicable(s: S, e: E) = inner.isApplicable(s, e)

    override fun apply(s: S, e: E) = inner.apply(s, e)

    @Suppress("unused")
    inline fun <reified TE: E> onlyIf(crossinline predicate:(S, TE) -> Boolean): MutableTransition<S, E> {
        editIsApplicable { s, e, isApplicable ->
            isApplicable && e is TE && predicate(s, e)
        }

        return this
    }

    @Suppress("unused")
    fun doAction(action:(S, E) -> Unit): MutableTransition<S, E> {
        editApply { s, e, newState ->
            action(s, e)
            newState
        }

        return this
    }

    @Suppress("unused")
    fun mapState(mapper:(S) -> S): MutableTransition<S, E> {
        editApply { _, _, newState ->
            mapper(newState)
        }

        return this
    }
}

class CompositeTransition<S: Any, E: Any> : Transition<S, E> {
    private val transitions: MutableList<Transition<S, E>> = ArrayList()

    override fun isApplicable(s: S, e: E): Boolean {
        return transitions.any {
            it.isApplicable(s, e)
        }
    }

    override fun apply(s: S, e: E): S {
        for (transition in transitions) {
            val (applied, newState) = transition.applyIfApplicable(s, e)

            if (applied) {
                return newState
            }
        }

        throw NoSuchElementException()
    }

    override fun applyIfApplicable(s: S, e: E): Pair<Boolean, S> {
        return try {
            val newState = apply(s, e)
            Pair(true, newState)
        } catch (ex: NoSuchElementException) {
            Pair(false, s)
        }
    }

    fun add(t: Transition<S, E>) {
        transitions.add(t)
    }
}
