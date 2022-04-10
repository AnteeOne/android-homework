package otus.homework.coroutines.data.network

import otus.homework.coroutines.data.dto.Cat
import otus.homework.coroutines.data.dto.Fact
import retrofit2.http.GET

interface CatsService {

    @GET("facts/random?animal_type=cat")
    suspend fun getCatFact(): Fact

    @GET("${RetrofitSettings.BASE_URL2}meow")
    suspend fun getCat(): Cat
}