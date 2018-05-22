package com.tarasantoshchuk.rx_workflow.core

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import com.squareup.coordinators.Coordinators
import com.tarasantoshchuk.rx_workflow.ui.ViewFactory
import io.reactivex.disposables.CompositeDisposable


abstract class WorkflowActivity : Activity() {
    private lateinit var workflow: Workflow

    private val resultDisposable = CompositeDisposable()

    abstract fun createWorkflow(): Workflow

    abstract fun createViewFactory(): ViewFactory

    abstract fun setContentView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()

        workflow = lastNonConfigurationInstance as Workflow? ?: createWorkflow()

        workflow
                .finish()
                .doOnSubscribe {
                    resultDisposable.add(it)
                }
                .doOnSuccess {
                    finish()
                }
                .subscribe()


        val rootView: ViewGroup = getRootView()
        val viewFactory = createViewFactory()

        Coordinators.installBinder(rootView, viewFactory)

        workflow.screen()
                .doOnSubscribe {
                    resultDisposable.add(it)
                }
                .subscribe { ws ->
                    viewFactory.
                            switchToScreen(ws, rootView)
                }

        if (savedInstanceState == null) {
            workflow.start()
        }
    }

    abstract fun getRootView(): ViewGroup

    override fun onBackPressed() {
        if (!workflow.back()) {
            super.onBackPressed()
        }
    }

    override fun onRetainNonConfigurationInstance(): Any {
        return workflow
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            workflow.finish()
        }

        resultDisposable.clear()
    }
}