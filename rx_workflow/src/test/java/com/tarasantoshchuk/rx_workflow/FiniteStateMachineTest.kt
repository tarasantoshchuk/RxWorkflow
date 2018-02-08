package com.tarasantoshchuk.rx_workflow

import com.tarasantoshchuk.rx_workflow.util.FiniteStateMachine
import com.tarasantoshchuk.rx_workflow.util.invoke
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

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
    fun accept_NoTransition() {
        stateMachine.startWith(TestStates.STATE1)

        stateMachine.accept(TestEvents.EVENT)

        assertSame(TestStates.STATE1, stateMachine.state())
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