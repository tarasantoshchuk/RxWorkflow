package com.tarasantoshchuk.finitestatemachine.utils

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable


fun <T> Observable<T>.disposeWith(disposables: CompositeDisposable): Observable<T> {
    return doOnSubscribe {
        disposables.add(it)
    }
}