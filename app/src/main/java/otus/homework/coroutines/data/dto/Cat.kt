package otus.homework.coroutines.data.dto


import com.google.gson.annotations.SerializedName

data class Cat(
    @SerializedName("file")
    val file: String
)