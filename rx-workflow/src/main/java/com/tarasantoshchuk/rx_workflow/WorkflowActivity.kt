package com.tarasantoshchuk.rx_workflow

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.squareup.coordinators.Coordinators
import io.reactivex.disposables.CompositeDisposable


abstract class WorkflowActivity<in I, R: Bundleable> : Activity() {

    companion object {
        const val KEY_INPUT: String = "KEY_INPUT"
        const val KEY_RESULT: String = "KEY_RESULT"
    }

    private lateinit var workflow: Workflow<I, R>

    private val resultDisposable = CompositeDisposable()

    abstract fun createWorkflow(): Workflow<I, R>

    abstract fun createViewFactory(): ViewFactory

    abstract fun setContentView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()

        workflow = lastNonConfigurationInstance as Workflow<I, R>? ?: createWorkflow()

        workflow
                .result()
                .doOnSubscribe{
                    resultDisposable.add(it)
                }
                .doOnComplete(this::finish)
                .subscribe(this::finish) {

                }


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
            workflow.start(intent.getSerializableExtra(KEY_INPUT) as I)
        }
    }

    abstract fun getRootView(): ViewGroup

    private fun finish(result: R) {
        setResult(Activity.RESULT_OK, result.putInto(KEY_RESULT, Intent()))
        finish()
    }

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
            workflow.abort()
        }

        resultDisposable.clear()
    }
}