package com.tarasantoshchuk.rx_workflow

import android.transition.AutoTransition
import android.transition.TransitionSet
import android.view.View
import android.view.ViewGroup

open class TransitionBuilder {
    open fun onExitTransitionSetup(root: ViewGroup, view: View) {
        root.removeView(view)
    }

    open fun onEnterTransitionSetup(root: ViewGroup, view: View) {
        root.addView(view)
    }

    open fun contributeEnterTransition(transition: TransitionSet, view: View) {
        transition.addTransition(AutoTransition().addTarget(view))
    }

    open fun contributeExitTransition(transition: TransitionSet, view: View) {
        transition.addTransition(AutoTransition().addTarget(view))
    }
}
