package com.tarasantoshchuk.finitestatemachine.auth_flow.logged_in

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.tarasantoshchuk.finitestatemachine.R
import com.tarasantoshchuk.finitestatemachine.auth_flow.AuthorizationBrick.UserData
import com.tarasantoshchuk.finitestatemachine.auth_flow.logged_in.LoggedInScreen.Events
import com.tarasantoshchuk.finitestatemachine.commons.BaseScreenCoordinator

class LoggedInCoordinator(screen: LoggedInScreen) : BaseScreenCoordinator<LoggedInScreen, Events, UserData>(screen) {
    @BindView(R.id.user_info)
    internal lateinit var userInfo: TextView

    @OnClick(R.id.log_out)
    fun onLogOut() {
        eventsHandler().onLogOut()
    }

    @SuppressLint("SetTextI18n")
    override fun attach(view: View) {
        super.attach(view)

        screenData().subscribe {
            userInfo.text = "Hello, ${it.name}. Your email is ${it.email}"
        }
    }
}
