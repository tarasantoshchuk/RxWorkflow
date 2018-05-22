package com.tarasantoshchuk.rx_workflow.butterknife

import android.transition.TransitionSet
import android.view.View
import android.view.ViewGroup
import com.tarasantoshchuk.rx_workflow.ui.TransitionBuilder


open class ButterKnifeTransitionBuilder: TransitionBuilder() {
    final override fun onExitTransitionSetup(root: ViewGroup, view: View) {
        withButterknife(view) {
            doOnExitTransitionSetup(root, view)
        }
    }

    open fun doOnExitTransitionSetup(root: ViewGroup, view: View) {
        super.onExitTransitionSetup(root, view)
    }

    final override fun onEnterTransitionSetup(root: ViewGroup, view: View) {
        withButterknife(view) {
            doOnEnterTransitionSetup(root, view)
        }
    }

    open fun doOnEnterTransitionSetup(root: ViewGroup, view: View) {
        super.onEnterTransitionSetup(root, view)
    }

    final override fun contributeEnterTransition(transition: TransitionSet, view: View) {
        withButterknife(view) {
            doContributeEnterTransition(transition, view)
        }
    }

    open fun doContributeEnterTransition(transition: TransitionSet, view: View) {
        super.contributeEnterTransition(transition, view)
    }

    final override fun contributeExitTransition(transition: TransitionSet, view: View) {
        withButterknife(view) {
            doContributeExitTransition(transition, view)
        }
    }

    open fun doContributeExitTransition(transition: TransitionSet, view: View) {
        super.contributeExitTransition(transition, view)
    }

    private fun withButterknife(view: View, block: () -> Unit) {
        val unbinder = butterknife.ButterKnife.bind(this, view)
        block()
        unbinder.unbind()
    }
}