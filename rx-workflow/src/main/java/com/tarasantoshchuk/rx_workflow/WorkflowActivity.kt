package com.tarasantoshchuk.rx_workflow

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.squareup.coordinators.Coordinators
import io.reactivex.disposables.Disposable


abstract class WorkflowActivity<in I, R: Bundleable> : Activity() {

    companion object {
        const val KEY_INPUT: String = "KEY_INPUT"
        const val KEY_RESULT: String = "KEY_RESULT"
    }

    private lateinit var workflow: Workflow<I, R>

    private lateinit var resultDisposable: Disposable

    abstract fun createWorkflow(): Workflow<I, R>

    abstract fun createViewFactory(): ViewFactory

    abstract fun setContentView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()

        workflow = createWorkflow()
        workflow
                .result()
                .doOnSubscribe{
                    resultDisposable = it
                }
                .doOnComplete(this::finish)
                .subscribe(this::finish) {

                }


        val rootView: ViewGroup = getRootView()
        val viewFactory = createViewFactory()

        Coordinators.installBinder(rootView, viewFactory)

        workflow.screen()
                .subscribe { ws ->
                    viewFactory.
                            switchToScreen(ws, rootView)
                }

        workflow.start(intent.getSerializableExtra(KEY_INPUT) as I)
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

    override fun onDestroy() {
        super.onDestroy()

        workflow.abort()
    }
}