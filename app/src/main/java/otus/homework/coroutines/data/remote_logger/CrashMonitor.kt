package otus.homework.coroutines.data.remote_logger

import android.util.Log

object CrashMonitor {

    /**
     * Pretend this is Crashlytics/AppCenter
     */
    fun trackWarning(t: Throwable) {
        Log.d("anteetag", "Crash monitor: ${t.message}")
    }
}
