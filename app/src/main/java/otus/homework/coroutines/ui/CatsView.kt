package otus.homework.coroutines.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso
import otus.homework.coroutines.R
import otus.homework.coroutines.data.dto.Cat
import otus.homework.coroutines.data.dto.Fact

class CatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ICatsView {

    var presenter: CatsPresenter? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        findViewById<Button>(R.id.button).setOnClickListener {
            presenter?.onInitComplete()
        }
    }

    override fun populate(data: Pair<Fact, Cat>) {
        with(data) {
            findViewById<TextView>(R.id.fact_textView).text = first.text
            val catImage = findViewById<ImageView>(R.id.catImage)
            Picasso.get().load(second.file).into(catImage)
        }
    }

    override fun onError(error: Error) {
        val errorText = when (error) {
            Error.TimeoutError -> "Не удалось получить ответ от сервером"
            is Error.UnknownError -> error.message
        }
        Toast.makeText(context, errorText, Toast.LENGTH_LONG).show()
    }
}

interface ICatsView {

    fun populate(data: Pair<Fact, Cat>)
    fun onError(error: Error)
}

// im too lazy for updating kotlin version to 1.5 and using sealed interfaces instead :D
sealed class Error {
    object TimeoutError : Error()
    data class UnknownError(val message: String) : Error()
}