package otus.homework.coroutines.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import otus.homework.coroutines.data.dto.Cat
import otus.homework.coroutines.data.dto.Fact
import otus.homework.coroutines.data.network.CatsService
import otus.homework.coroutines.data.remote_logger.CrashMonitor
import otus.homework.coroutines.di.DiContainer
import java.net.SocketException
import java.net.SocketTimeoutException

class CatsViewModel : ViewModel() {

    val catsService: CatsService = DiContainer().service

    var parentJob: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, t ->
        cancelParentJob()
        CrashMonitor.trackWarning(t)
    }

    private var _data: MutableStateFlow<Result> = MutableStateFlow(Result.Empty)
    val data: StateFlow<Result> = _data

    fun onInitComplete() {
        loadCat()
    }

    fun loadCat() {
        if (parentJob != null) return
        parentJob = viewModelScope.launch(exceptionHandler) {
            coroutineScope {
                val fact = async {
                    catsService.getCatFact()
//                    throw RuntimeException("lol kek cheburek")
// Exception will be implemented in the Deferred fact object
                }
                val cat = async {
                    catsService.getCat()
                }
                try {
                    _data.value = Result.Success(Pair(fact.await(), cat.await()))
                } catch (t: Throwable) {
                    when (t) {
                        is SocketException,
                        is SocketTimeoutException -> Result.Error(t)
                        else -> throw t // Delegating not busyness exceptions to exceptionHandler
                    }
                }
                cancelParentJob()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelParentJob()
    }

    fun cancelParentJob() {
        parentJob?.cancel()
        parentJob = null
    }
}

sealed class Result {
    object Empty : Result()
    data class Error(val t: Throwable) : Result()
    data class Success(val data: Pair<Fact, Cat>) : Result()
}
