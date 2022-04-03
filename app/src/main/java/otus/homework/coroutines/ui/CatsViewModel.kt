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
import java.net.SocketTimeoutException

class CatsViewModel(
    private val catsService: CatsService
) : ViewModel() {

    var parentJob: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, t ->
        when (t) {
            is CancellationException -> throw t
            is SocketTimeoutException -> _data.value = Result.Error(t) // + toast logic
            else -> {
                CrashMonitor.trackWarning(t)
                _data.value = Result.Error(t)
            }
        }
    }

    private var _data: MutableStateFlow<Result> = MutableStateFlow(Result.Empty)
    val data: StateFlow<Result> = _data

    fun onInitComplete() {
        parentJob = viewModelScope.launch {
            val fact = async { catsService.getCatFact() }
            val cat = async { catsService.getCat() }

        }
    }

    fun onClear() {
        parentJob?.cancel()
        parentJob = null
    }
}

sealed class Result {
    object Empty : Result()
    data class Error(val t: Throwable) : Result()
    data class Success(val data: Pair<Fact, Cat>) : Result()
}