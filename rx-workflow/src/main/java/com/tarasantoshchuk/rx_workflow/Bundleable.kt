package com.tarasantoshchuk.rx_workflow

import android.content.Intent
import android.os.Bundle

interface Bundleable {
    fun putInto(key: String, bundle: Bundle): Bundle
    fun putInto(key: String, intent: Intent): Intent
}
