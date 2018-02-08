package com.tarasantoshchuk.rx_workflow

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.squareup.coordinators.Coordinators
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposable


abstract class WorkflowActivity<in I, R> : AppCompatActivity() {
    companion object {
        val KEY_INPUT: String = "KEY_INPUT"
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
                .subscribe(object : MaybeObserver<R> {
                    override fun onSubscribe(d: Disposable) {
                        resultDisposable = d
                    }

                    override fun onSuccess(t: R) {

                    }

                    override fun onComplete() {

                    }

                    override fun onError(e: Throwable) {

                    }
                })


        val rootView: ViewGroup = window.decorView as ViewGroup
        val viewFactory = createViewFactory()

        Coordinators.installBinder(rootView, viewFactory)

        workflow
                .screen()
                .subscribe(
                        { ws -> viewFactory.createView(ws, rootView) }
                )


        workflow.start(intent.extras.getSerializable(KEY_INPUT) as I)
    }

    override fun onDestroy() {
        super.onDestroy()

        workflow.abort()
    }
}