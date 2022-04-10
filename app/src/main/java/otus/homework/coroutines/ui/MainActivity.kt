package otus.homework.coroutines.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import otus.homework.coroutines.R
import otus.homework.coroutines.data.dto.Cat
import otus.homework.coroutines.data.dto.Fact
import java.net.SocketException
import java.net.SocketTimeoutException

class MainActivity : AppCompatActivity() {

//    lateinit var catsPresenter: CatsPresenter
//    private val diContainer = DiContainer()

    private val viewModel by viewModels<CatsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)
        viewModel.onInitComplete()
        onObserve()
        findViewById<Button>(R.id.button).setOnClickListener {
            viewModel.loadCat()
        }

//        catsPresenter = CatsPresenter(diContainer.service)
//        view.presenter = catsPresenter
//        catsPresenter.attachView(view)
//        catsPresenter.onInitComplete()
    }

    private fun onObserve() {
        lifecycleScope.launch {
            viewModel.data.collect {
                when (it) {
                    is Result.Empty -> {
                        Log.d("anteetag", "View: Result is empty")
                    }
                    is Result.Error -> {
                        Log.d("anteetag", "View: Result is error")
                        onError(
                            when (it.t) {
                                is SocketException,
                                is SocketTimeoutException -> Error.TimeoutError
                                else -> Error.UnknownError(it.t.localizedMessage)
                            }
                        )
                    }
                    is Result.Success -> {
                        Log.d("anteetag", "Result is empty")
                        onSuccess(it.data)
                    }
                }
            }
        }
    }

    private fun onSuccess(data: Pair<Fact, Cat>) {
        with(data) {
            findViewById<TextView>(R.id.fact_textView).text = first.text
            val catImage = findViewById<ImageView>(R.id.catImage)
            Picasso.get().load(second.file).into(catImage)
        }
    }

    private fun onError(error: Error) {
        val errorText = when (error) {
            Error.TimeoutError -> "Не удалось получить ответ от сервером"
            is Error.UnknownError -> error.message
        }
        Toast.makeText(this, errorText, Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
//        if (isFinishing) {
//            catsPresenter.detachView()
//        }
        viewModel.cancelParentJob()
        super.onStop()
    }
}
