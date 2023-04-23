import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import io.github.serpro69.kfaker.Faker
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import ir.amirab.debugboard.backend.DebugBoardBackend
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.core.plugin.logger.LogData
import ir.amirab.debugboard.core.plugin.logger.LogLevel
import ir.amirab.debugboard.plugin.network.ktor.KtorDebugBoard
import ir.amirab.debugboard.plugin.network.okhttp.OkHttpDebugBoardInterceptor
import ir.amirab.debugboard.plugin.watcher.compose.AddWatch
import ir.amirab.debugboard.plugin.watcher.flow.addWatch
import ir.amirab.debugboard.plugin.watcher.period.addWatch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.time.Duration.Companion.seconds

private val scope = CoroutineScope(Dispatchers.Default)

/**
 * for generate fake data
 */
private val faker = Faker()

/**
 * Ktor client with Debug Board plugin
 */
private val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
    install(Logging) {
        this.logger = Logger.SIMPLE
    }
    install(KtorDebugBoard)
}

/**
 * retrofit with Okhttp client with Debug Board interceptor
 */
private val retrofit = Retrofit
    .Builder()
    .baseUrl("https://jsonplaceholder.typicode.com")
    .addConverterFactory(GsonConverterFactory.create())
    .client(
        OkHttpClient
            .Builder()
            .addInterceptor(OkHttpDebugBoardInterceptor())
            .build()
    ).build()

interface QuestionApi {
    @GET("https://jsonplaceholder.typicode.com/todos/{code}")
    suspend fun getQuestions(
        @Path("code") code: String
    ): String
}

/**
 * sample class to watch its state
 */
class AClass {
    var a: String = "ab"
    var b = listOf<String>("a", "b")
    var c = mapOf("a" to "b")
}

fun main() {
    // start debug board backend server on this device
    DebugBoardBackend().startWithDefaultServer()

    addVariableAndUpdate()

    sendRequestRandomely()

    addRandomLogs()

    // A simple jetpack compose application that you watch your states
    singleWindowApplication(
        title = "Debug Board Sample",
        state = WindowState(width = 400.dp, height = 768.dp),
    ) {
        MaterialTheme(darkColors()) {
            RenderScreen()
        }
    }
}


fun addRandomLogs() {
    scope.launch {
        while (isActive) {
            delay(5.seconds)
            DebugBoard.Default.logger.log(
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

fun sendRequestRandomely() {
    val api = retrofit.create(QuestionApi::class.java)

    scope.launch {
        while (isActive) {
            delay(2000)
            kotlin.runCatching {
                val randomNumber = (1..10).random().toString()
                client.get("https://jsonplaceholder.typicode.com/todos/$randomNumber")
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}

/**
 * These are some sample to demonstrate how you can watch your variables
 * You can add your own by implementing [Watchable] interface
 **/
fun addVariableAndUpdate() {

    // watch any stateflow
    val stateFlow = MutableStateFlow(emptyList<String>())
    scope.launch {
        var counter = 0
        while (isActive) {
            stateFlow.value = stateFlow.value + "${counter++}"
            delay(5000)
        }
    }

    //another sample stateflow with any instance
    val aClass = MutableStateFlow(AClass())
    addWatch("anyInstance", aClass)

    // a cold flow can be added to watch, but it needs to have an initial value
    addWatch<String?>("flowSample", null, flow {
        delay(10.seconds)
        emit("after 10 seconds I will be there")
    })

    //simple variable which is not an observable we can use a periodic approach to watch that value
    var pp = "hello"
    addWatch(
        "periodicSample",
        period = 1000
    ) { pp }
    scope.launch {
        var count = 0
        while (isActive) {
            pp = "${count++}"
            delay(10)
        }
    }
}

/**
 * Render a simple compose app which add text fields and add watch their values
 */
@Composable
fun RenderScreen() {
    Scaffold {
        val (count, setCount) = remember {
            mutableStateOf(1)
        }
        Column {
            Row {
                Button({ setCount(count + 1) }) {
                    Text("+")
                }
                Button({ setCount(count - 1) }) {
                    Text("-")
                }
            }
            repeat(count) {
                TextFieldView(it)
            }
        }
    }
}

@Composable
fun TextFieldView(index: Int) {
    val (text, setText) = remember { mutableStateOf("") }
    //add watch / also remove watch automatically when composable exits from ui tree
    AddWatch("text$index", text)
    TextField(text, onValueChange = {
        setText(it)
    })
}