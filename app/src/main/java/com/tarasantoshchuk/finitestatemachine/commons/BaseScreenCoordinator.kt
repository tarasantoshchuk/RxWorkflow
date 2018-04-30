package com.tarasantoshchuk.finitestatemachine.commons

import android.view.View
import androidx.core.widget.toast
import com.tarasantoshchuk.finitestatemachine.utils.disposeWith
import com.tarasantoshchuk.rx_workflow.butterknife.ButterknifeScreenCoordinator
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable


open class BaseScreenCoordinator<WS: BaseWorkflowScreen<D, E>, E, D>(private val screen: WS): ButterknifeScreenCoordinator<WS, E, D>(screen) {
    private val disposables = CompositeDisposable()

    override fun attach(view: View) {
        super.attach(view)

        commonData()
                .map{
                    it.toast
                }
                .subscribe {
                    view.context.toast(it)
                }
    }

    override fun detach(view: View) {
        super.detach(view)

        disposables.clear()
    }

    protected fun <T> Observable<T>.disposeOnDetach(): Observable<T> {
        return disposeWith(disposables)
    }

    protected fun commonData(): Observable<CommonScreenData> {
        return screen.commonData.disposeOnDetach()
    }
}