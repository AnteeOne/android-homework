package otus.homework.coroutines.ui

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import otus.homework.coroutines.data.network.CatsService
import otus.homework.coroutines.data.remote_logger.CrashMonitor
import java.net.SocketTimeoutException

class CatsPresenter(
    private val catsService: CatsService
) {

    private var _catsView: ICatsView? = null
    private val presenterScope = PresenterScope()
    private var catsJob: Job? = null

    fun onInitComplete() {
        catsJob = presenterScope.launch {
            try {
//                val fact = async { catsService.getCatFact() }    parallel start, but without exception handling
//                val cat = async { catsService.getCat() }
                val fact = catsService.getCatFact()
                val cat = catsService.getCat()
                with(Dispatchers.Main) { // if someone will add dispatcher in launch coroutine starter
                    _catsView?.populate(Pair(fact,cat))
                }
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is SocketTimeoutException -> _catsView?.onError(Error.TimeoutError)
                    else -> {
                        CrashMonitor.trackWarning(e)
                        _catsView?.onError(Error.UnknownError(e.localizedMessage))
                    }
                }
            }
        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun detachView() {
        _catsView = null
        cancelJob()
    }

    fun cancelJob() {
        catsJob?.cancel()
        catsJob = null
    }
}