package sbs.pros.parking.utils

import android.R
import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.google.android.datatransport.runtime.scheduling.SchedulingConfigModule_ConfigFactory.config
import java.io.IOException
import java.io.InputStream
import java.util.*


object Helper {
    private const val TAG = "Helper"

    fun getConfigValue(context: Context, name: String?): String? {
        val resources: Resources = context.getResources()
        try {
//            val rawResource: InputStream = resources.openRawResource(R.raw.config)
            val properties = Properties()
//            properties.load(rawResource)
            return properties.getProperty(name)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Unable to find the config file: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to open config file.")
        }
        return null
    }
}