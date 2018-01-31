package com.tarasantoshchuk.rx_workflow


import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.coordinators.Coordinator
import com.squareup.coordinators.CoordinatorProvider
import java.util.*

open class ViewFactory : CoordinatorProvider {
    private val mLayoutMaps = HashMap<String, Int>()
    private val mConverterMap = HashMap<String, (WorkflowScreen<*, *>) -> ScreenCoordinator<*, *, *>>()

    fun createView(screen: WorkflowScreen<*, *>, root: ViewGroup) {
        val key = screen.key
        val layoutId = mLayoutMaps[key] ?: return

        root.removeAllViews()

        val view = LayoutInflater.from(root.context).inflate(layoutId, root, false)
        view.setTag(R.id.key, key)
        view.setTag(R.id.workflow_screen, screen)

        root.addView(view)
    }

    protected fun bindLayout(key: String, @LayoutRes layoutId: Int, coordinatorConverter: (WorkflowScreen<*, *>) -> ScreenCoordinator<*, *, *>) {
        mLayoutMaps.put(key, layoutId)
        mConverterMap.put(key, coordinatorConverter)
    }

    override fun provideCoordinator(view: View): Coordinator? {
        return mConverterMap[view.getTag(R.id.key) as String]?.invoke(view.getTag(R.id.workflow_screen) as WorkflowScreen<*, *>)
    }
}
