package otus.homework.coroutines.ui

import kotlinx.coroutines.CancellationException
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
        loadCat()
    }

    fun loadCat() {
        if (catsJob != null) return
        catsJob = presenterScope.launch {
            try {
                val fact = catsService.getCatFact()
                val cat = catsService.getCat()
                _catsView?.populate(Pair(fact, cat))
                cancelCatJob()
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
        cancelCatJob()
    }

    fun cancelCatJob() {
        catsJob?.cancel()
        catsJob = null
    }
}
