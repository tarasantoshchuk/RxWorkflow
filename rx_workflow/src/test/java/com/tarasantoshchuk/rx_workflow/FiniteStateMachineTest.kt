package com.tarasantoshchuk.rx_workflow

import com.tarasantoshchuk.rx_workflow.finite_state_machine.MutableTransition
import com.tarasantoshchuk.rx_workflow.util.FiniteStateMachine
import com.tarasantoshchuk.rx_workflow.util.invoke
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FiniteStateMachineTest {
    private lateinit var stateMachine: TestStateMachine

    @Before
    fun setUp() {
        stateMachine = TestStateMachine()
    }

    @Test
    fun startWith() {
        stateMachine.startWith(TestStates.STATE1)

        assertSame(TestStates.STATE1, stateMachine.state())
    }

    @Test(expected = IllegalStateException::class)
    fun accept_WithoutStartState_Exception() {
        stateMachine {
            onState(TestStates.STATE1) {
            }
        }

        stateMachine.accept(TestEvents.EVENT2_1)
    }

    @Test
    fun accept() {
        var transitionDone = false
        stateMachine {
            onState(TestStates.STATE2) {
                transitionDone = true
            }
        }

        stateMachine.startWith(TestStates.STATE1)

        stateMachine.accept(TestEvents.EVENT1_2)

        assertTrue(transitionDone)
    }

    @Test
    fun anyState() {
        var anyStateCalled = false
        stateMachine {
            onAnyState {
                anyStateCalled = true
            }
        }

        stateMachine.startWith(TestStates.STATE1)

        stateMachine.accept(TestEvents.EVENT1_2)

        assertTrue(anyStateCalled)
    }

    @Test
    fun anyState_WithoutAccept() {
        var anyStateCalled = false
        stateMachine {
            onAnyState {
                anyStateCalled = true
            }
        }

        assertFalse(anyStateCalled)
    }

    @Test
    fun accept_NoTransition() {
        stateMachine.startWith(TestStates.STATE1)

        stateMachine.accept(TestEvents.EVENT)

        assertSame(TestStates.STATE1, stateMachine.state())
    }

    @Test
    fun anyState_WithoutStartState() {
        var anyStateCalled = false
        var exceptionRaised = false
        stateMachine {
            onAnyState {
                anyStateCalled = true
            }
        }

        try {
            stateMachine.accept(TestEvents.EVENT2_1)
        } catch (e: IllegalStateException) {
            exceptionRaised = true
        }
        assertTrue(exceptionRaised)
        assertFalse(anyStateCalled)
    }

    @Test
    fun transition_onlyIfFalse() {
        var transitionCalled = false
        val stateMachineLocal = object : FiniteStateMachine<TestStates>() {
            init {
                transition(TestStates.STATE1, TestEvents.EVENT1_2, TestStates.STATE2)
                        .onlyIf<TestEvents>({ _, _ -> false })
                onState(TestStates.STATE2) {
                    transitionCalled = true
                }
            }
        }

        stateMachineLocal.startWith(TestStates.STATE1)

        stateMachineLocal.accept(TestEvents.EVENT1_2)

        assertFalse(transitionCalled)
    }

    @Test
    fun transition_doAction() {
        var actionCalled = false
        val stateMachineLocal = object : FiniteStateMachine<TestStates>() {
            init {
                transition(TestStates.STATE1, TestEvents.EVENT1_2, TestStates.STATE2)
                        .doAction { _, _ ->
                            actionCalled = true
                        }
            }
        }

        stateMachineLocal.startWith(TestStates.STATE1)

        stateMachineLocal.accept(TestEvents.EVENT1_2)

        assertTrue(actionCalled)
    }

    @Test
    fun transition_isApplicable() {
        val stateMachineLocal = object : FiniteStateMachine<TestStates>() {
            val transitionValue: MutableTransition<TestStates, Event>

            init {
                transitionValue = transition(TestStates.STATE1, TestEvents.EVENT1_2, TestStates.STATE2)
            }
        }

        assertTrue(stateMachineLocal.transitionValue.isApplicable(TestStates.STATE1, TestEvents.EVENT1_2))
    }

    @Test
    fun transition_editApplicable() {
        val stateMachineLocal = object : FiniteStateMachine<TestStates>() {
            val transitionValue: MutableTransition<TestStates, Event>

            init {
                transitionValue = transition(TestStates.STATE1, TestEvents.EVENT1_2, TestStates.STATE2)
            }
        }

        stateMachineLocal.transitionValue.editIsApplicable { state, event, applicable ->
            if (state == TestStates.STATE1 && event == TestEvents.EVENT1_2) false
            else applicable
        }

        assertFalse(stateMachineLocal.transitionValue.isApplicable(TestStates.STATE1, TestEvents.EVENT1_2))
    }
}

enum class TestStates {
    STATE1, STATE2
}

enum class TestEvents : Event {
    EVENT1_2, EVENT2_1, EVENT
}

class TestStateMachine : FiniteStateMachine<TestStates>() {
    init {
        transition(TestStates.STATE1, TestEvents.EVENT1_2, TestStates.STATE2)
        transition(TestStates.STATE2, TestEvents.EVENT2_1, TestStates.STATE1)
    }
}