package com.tarasantoshchuk.rx_workflow.butterknife

import android.support.transition.TransitionSet
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.tarasantoshchuk.rx_workflow.TransitionBuilder


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
        val unbinder = ButterKnife.bind(this, view)
        block()
        unbinder.unbind()
    }
}