package com.tarasantoshchuk.rx_workflow


import android.support.annotation.LayoutRes
import android.support.transition.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.coordinators.Coordinator
import com.squareup.coordinators.CoordinatorProvider
import java.util.*

open class ViewFactory : CoordinatorProvider {
    private val mLayouts = HashMap<String, Int>()
    private val mConverters = HashMap<String, (WorkflowScreen<*, *>) -> ScreenCoordinator<*, *, *>>()
    private val mTransitionBuilders = HashMap<String, TransitionBuilder>()

    fun createView(screen: WorkflowScreen<*, *>, root: ViewGroup) {
        val key = screen.key

        val previousView = root.getChildAt(0)
        val nextView = createNextView(root, key, screen)

        launchTransition(previousView, nextView, root)
    }

    private fun launchTransition(previousView: View?, nextView: View, root: ViewGroup) {
        val inTransitionBuilder = provideTransitionBuilder(nextView)
        val outTransitionBuilder = provideTransitionBuilder(previousView)

        val transition = TransitionSet()

        inTransitionBuilder.contributeEnterTransition(transition, nextView)
        outTransitionBuilder?.contributeExitTransition(transition, previousView!!)

        TransitionManager.beginDelayedTransition(root, transition)

        inTransitionBuilder.onEnterTransitionSetup(root, nextView)
        outTransitionBuilder?.onExitTransitionSetup(root, previousView!!)
    }

    private fun createNextView(root: ViewGroup, key: String, screen: WorkflowScreen<*, *>): View {
        val layoutId = mLayouts[key] ?: throw IllegalArgumentException()

        val view = LayoutInflater.from(root.context).inflate(layoutId, root, false)
        view.setTag(R.id.key, key)
        view.setTag(R.id.workflow_screen, screen)
        return view
    }

    protected fun bindLayout(key: String, @LayoutRes layoutId: Int, transitionBuilder: TransitionBuilder = TransitionBuilder(), coordinatorConverter: (WorkflowScreen<*, *>) -> ScreenCoordinator<*, *, *>) {
        mLayouts[key] = layoutId
        mTransitionBuilders[key] = transitionBuilder
        mConverters[key] = coordinatorConverter
    }

    override fun provideCoordinator(view: View): Coordinator? {
        return mConverters[view.getTag(R.id.key) as? String]?.invoke(view.getTag(R.id.workflow_screen) as WorkflowScreen<*, *>)
    }

    private fun provideTransitionBuilder(view: View?): TransitionBuilder? {
        if (view == null) {
            return null
        }

        return provideTransitionBuilder(view)
    }

    @JvmName("provideTransitionBuilderNonNull")
    private fun provideTransitionBuilder(view: View): TransitionBuilder {
        return mTransitionBuilders[view.getTag(R.id.key)] ?: throw IllegalArgumentException()
    }
}
