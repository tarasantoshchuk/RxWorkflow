package com.tarasantoshchuk.rx_workflow.core

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.squareup.coordinators.Coordinators
import com.tarasantoshchuk.rx_workflow.ui.ViewFactory
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable


abstract class WorkflowActivity : AppCompatActivity() {
    private lateinit var workflow: Workflows

    private val resultDisposable = CompositeDisposable()

    abstract fun createWorkflows(): Workflows

    abstract fun setContentView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()

        workflow = lastCustomNonConfigurationInstance as Workflows? ?: createWorkflows()

        workflow
                .finish()
                .doOnSubscribe {
                    resultDisposable.add(it)
                }
                .doOnComplete {
                    finish()
                }
                .subscribe()

        workflow.screen(window.decorView as ViewGroup)
                .doOnSubscribe {
                    resultDisposable.add(it)
                }
                .subscribe()

        if (savedInstanceState == null) {
            workflow.start()
        }
    }

    override fun onBackPressed() {
        if (!workflow.back()) {
            super.onBackPressed()
        }
    }

    override fun onRetainCustomNonConfigurationInstance(): Any {
        return workflow
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            workflow.doFinish()
        }

        resultDisposable.clear()
    }
}

class Workflows(block: Workflows.() -> Unit) {
    private var workflowProviders: ArrayList<WorkflowProvider> = ArrayList()


    fun registerWorkflow(workflow: Workflow, factory: ViewFactory, rootView: (ViewGroup) -> ViewGroup) {
        workflowProviders.add(WorkflowProvider(workflow, factory, rootView))
    }

    fun finish(): Completable {
        return Observable.fromIterable(workflowProviders)
                .flatMap {
                    it.workflow.finish().toObservable()
                }
                .ignoreElements()
    }

    fun screen(workflowRoot: ViewGroup): Completable {
        return Observable
                .fromIterable(workflowProviders)
                .flatMap { provider ->
                    val root = provider.rootView(workflowRoot)
                    val factory = provider.factory

                    provider
                            .workflow
                            .screen()
                            .doOnSubscribe {
                                Coordinators.installBinder(root, factory)
                            }
                            .doOnNext {
                                factory.switchToScreen(it, root)
                            }
                }
                .ignoreElements()
    }

    fun doFinish() {
        Observable.fromIterable(workflowProviders)
                .subscribe {
                    it.workflow.doFinish(TerminationKey.FINISH)
                }
    }

    fun start() {
        workflowProviders
                .map {
                    it.workflow
                }
                .forEach {
                    it.start()
                }
    }

    fun back(): Boolean {
        workflowProviders
                .map {
                    it.workflow
                }
                .onEach {
                    if (it.back()) {
                        return@back true
                    }
                }

        return false
    }

    init {
        this.block()
    }

}

data class WorkflowProvider(val workflow: Workflow, val factory: ViewFactory, val rootView: (ViewGroup) -> ViewGroup)