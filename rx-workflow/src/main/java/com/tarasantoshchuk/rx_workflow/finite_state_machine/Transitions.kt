package com.tarasantoshchuk.rx_workflow.finite_state_machine

import com.tarasantoshchuk.rx_workflow.Event
import java.util.ArrayList

interface Transition<S : Any> {
    fun isApplicable(s: S, e: Event): Boolean
    fun apply(s: S, e: Event): S

    fun applyIfApplicable(s: S, e: Event): Pair<Boolean, S> {
        if (isApplicable(s, e)) {
            return Pair(true, apply(s, e))
        }

        return Pair(false, s)
    }
}

interface EditableTransition<S : Any, Event : Any> : Transition<S> {
    fun editIsApplicable(applicableAction: (S, Event, Boolean) -> Boolean): EditableTransition<S, Event>
    fun editApply(applyAction: (S, Event, S) -> S): EditableTransition<S, Event>
}

open class SimpleEditableTransition<S : Any>(private val inner: Transition<S>) : EditableTransition<S, Event> {
    override fun isApplicable(s: S, e: Event) = inner.isApplicable(s, e)

    override fun apply(s: S, e: Event) = inner.apply(s, e)

    override fun editApply(applyAction: (S, Event, S) -> S): EditableTransition<S, Event> =
            object : SimpleEditableTransition<S>(this) {
                override fun apply(s: S, e: Event): S {
                    return applyAction(s, e, inner.apply(s, e))
                }
            }

    override fun editIsApplicable(applicableAction: (S, Event, Boolean) -> Boolean): EditableTransition<S, Event> =
            object : SimpleEditableTransition<S>(this) {
                override fun isApplicable(s: S, e: Event): Boolean {
                    return applicableAction(s, e, inner.isApplicable(s, e))
                }
            }
}

class MutableTransition<S : Any>(transition: Transition<S>) : EditableTransition<S, Event> {
    private var inner: EditableTransition<S, Event> = SimpleEditableTransition(transition)

    override fun editIsApplicable(applicableAction: (S, Event, Boolean) -> Boolean): EditableTransition<S, Event> {
        inner = inner.editIsApplicable(applicableAction)
        return inner
    }

    override fun editApply(applyAction: (S, Event, S) -> S): EditableTransition<S, Event> {
        inner = inner.editApply(applyAction)
        return inner
    }

    override fun isApplicable(s: S, e: Event) = inner.isApplicable(s, e)

    override fun apply(s: S, e: Event) = inner.apply(s, e)

    inline fun <reified TE : Event> onlyIf(crossinline predicate: (S, TE) -> Boolean): MutableTransition<S> {
        editIsApplicable { s, e, isApplicable ->
            isApplicable && e is TE && predicate(s, e)
        }

        return this
    }

    fun doAction(action: (S, Event) -> Unit): MutableTransition<S> {
        editApply { s, e, newState ->
            action(s, e)
            newState
        }

        return this
    }

    fun mapState(mapper: (S) -> S): MutableTransition<S> {
        editApply { _, _, newState ->
            mapper(newState)
        }

        return this
    }
}

class CompositeTransition<S : Any> : Transition<S> {
    private val transitions: MutableList<Transition<S>> = ArrayList()

    override fun isApplicable(s: S, e: Event): Boolean {
        return transitions.any {
            it.isApplicable(s, e)
        }
    }

    override fun apply(s: S, e: Event): S {
        for (transition in transitions) {
            val (applied, newState) = transition.applyIfApplicable(s, e)

            if (applied) {
                return newState
            }
        }

        throw NoSuchElementException()
    }

    override fun applyIfApplicable(s: S, e: Event): Pair<Boolean, S> {
        return try {
            val newState = apply(s, e)
            Pair(true, newState)
        } catch (ex: NoSuchElementException) {
            Pair(false, s)
        }
    }

    fun add(t: Transition<S>) {
        transitions.add(t)
    }
}