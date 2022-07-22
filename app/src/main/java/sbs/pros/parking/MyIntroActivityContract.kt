package sbs.pros.parking

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class MyIntroActivityContract : ActivityResultContract<String, Boolean?>() {

    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, IntroActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent?.getBooleanExtra("login", false)
    }
}