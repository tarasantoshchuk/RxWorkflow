package com.tarasantoshchuk.finitestatemachine.auth_flow

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class AuthorizationBrick {
    private var userData: UserData? = null

    fun logIn(email: String, password: String): Completable {
        return switchUserWithFailChance(UserData("%USER_NAME%", email))
    }

    private fun switchUserWithFailChance(newUserData: UserData?): Completable {
        return Completable.fromAction {
            Thread.sleep(2000)

            if (Random().nextBoolean()) {
                throw Exception()
            }

            userData = newUserData
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun logOut(): Completable {
        return switchUserWithFailChance(null)
    }

    fun userData(): Observable<UserData> {
        return Observable.just(userData)
    }


    data class UserData(val name: String, val email: String)
}

