import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import io.github.serpro69.kfaker.Faker
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import ir.amirab.debugboard.plugin.network.okhttp.OkHttpDebugBoardInterceptor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.DebugBoardBackend
import ir.amirab.debugboard.plugin.network.ktor.KtorDebugBoard
import project.LocalDebugBoard
import ir.amirab.debugboard.core.plugins.FlowWatchable
import ir.amirab.debugboard.core.plugins.LogData
import ir.amirab.debugboard.core.plugins.LogLevel
import project.ui.variablewatcher.DebagBoardView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

val debugBoard = DebugBoard()
val scope = CoroutineScope(Dispatchers.Default)
val faker = Faker()
val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
    install(Logging) {
        this.logger=Logger.SIMPLE
    }
//    install(KtorDebugBoard) {
//        debugBoard(debugBoard)
//    }
}
val retrofit = Retrofit
    .Builder()
    .baseUrl("https://jsonplaceholder.typicode.com")
    .addConverterFactory(GsonConverterFactory.create())
    .client(
        OkHttpClient
            .Builder()
            .addInterceptor(OkHttpDebugBoardInterceptor(debugBoard))
            .build()
    ).build()

interface QuestionApi {
    @GET("https://jsonplaceholder.typicode.com/todos/{code}")
    suspend fun getQuestions(
        @Path("code") code: String
    ): String
}

class AClass {
    var a: String = "ab"
    var b = listOf<String>("a", "b")
    var c = mapOf("a" to "b")
}

fun main() {
    DebugBoardBackend(debugBoard = debugBoard)
        .startWithDefaultServer()
    addVariableAndUpdate()
    sendRequestRandomely()
    addLogRandomely()
    singleWindowApplication(
        title = "Code Viewer",
        state = WindowState(width = 400.dp, height = 768.dp),
    ) {
        MaterialTheme(darkColors()) {
            CompositionLocalProvider(
                LocalDebugBoard provides debugBoard
            ) {
                RenderScreen()
            }
        }
    }
}


fun addLogRandomely() {
    scope.launch {
        while (isActive) {
            delay(5.seconds)
            debugBoard.logger.log(
                LogData(
                    System.currentTimeMillis(),
                    listOf(
                        LogLevel.Debug,
                        LogLevel.Info,
                        LogLevel.Warning,
                        LogLevel.Error,
                    ).random(),
                    faker.random.randomString((20..140).random()),
                    faker.random.randomString(14)
                )
            )
        }
    }
}

@Serializable
data class ZZ(
    val x: String,
)

fun sendRequestRandomely() {
    val api = retrofit.create(QuestionApi::class.java)


    scope.launch {
        while (isActive) {
            delay(2000)
//            runCatching {
//                val randomNumber = (1..10).random().toString()
//                api.getQuestions(randomNumber)
//            }.onFailure {
//                it.printStackTrace()
//            }
            delay(1000)
            kotlin.runCatching {
                val randomNumber = (1..10).random().toString()
                client.get("https://jsonplaceholder.typicode.com/todos/$randomNumber")
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}

fun addVariableAndUpdate() {
    val x = MutableStateFlow(emptyList<String>())
    val y = MutableStateFlow(AClass())
    scope.launch {
        var counter = 0
        while (isActive) {
            x.value = x.value + "${counter++}"
            delay(5000)
        }
    }
    debugBoard.variableWatcher.addWatch(FlowWatchable("x", x))
    debugBoard.variableWatcher.addWatch(FlowWatchable("y", y))
    debugBoard.variableWatcher.addWatch(FlowWatchable("z", x.map {
        it.map { it.toInt() }.sum()
    }))
}

@Composable
fun RenderScreen() {
    Scaffold {
        DebagBoardView()
    }
}