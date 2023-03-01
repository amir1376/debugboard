import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import ir.amirab.debugboard.core.DebugBoard
import ir.amirab.debugboard.DebugBoardBackend
import project.LocalDebugBoard
import ir.amirab.debugboard.core.plugins.LogData
import ir.amirab.debugboard.core.plugins.LogLevel
import ir.amirab.debugboard.extentions.addWatch
import ir.amirab.debugboard.plugin.network.ktor.KtorDebugBoard
import ir.amirab.debugboard.plugin.watcher.compose.AddWatch
import kotlinx.coroutines.flow.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.time.Duration.Companion.seconds

val scope = CoroutineScope(Dispatchers.Default)
val faker = Faker()
val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
    install(Logging) {
        this.logger=Logger.SIMPLE
    }
    install(KtorDebugBoard)
}
val retrofit = Retrofit
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

class AClass {
    var a: String = "ab"
    var b = listOf<String>("a", "b")
    var c = mapOf("a" to "b")
}

fun main() {
    DebugBoardBackend().startWithDefaultServer()
    addVariableAndUpdate()
//    sendRequestRandomely()
//    addLogRandomely()
    singleWindowApplication(
        title = "Code Viewer",
        state = WindowState(width = 400.dp, height = 768.dp),
    ) {
        MaterialTheme(darkColors()) {
            CompositionLocalProvider(
                LocalDebugBoard provides DebugBoard.Default
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
    val stateFlow = MutableStateFlow(emptyList<String>())
    val aClass = MutableStateFlow(AClass())



    scope.launch {
        var counter = 0
        while (isActive) {
            stateFlow.value = stateFlow.value + "${counter++}"
            delay(5000)
        }
    }
    var pp="hello"
    addWatch("periodicSample",
        period=1000
    ){ pp }
    scope.launch {
        var count=0
        while (isActive){
            pp="${count++}"
            delay(10)
        }
    }

    addWatch<String?>("flowSample",null, flow {
        delay(10000)
        emit("after 10 seconds I will be there")
    })

    addWatch("stateFlowSample", stateFlow)

    addWatch("anyInstance", aClass)
}


@Composable
fun RenderScreen() {
    Scaffold {
        val (count,setCount)=remember{
            mutableStateOf(1)
        }
        Column {
            Row{
                Button({setCount(count+1)}){
                    Text("+")
                }
                Button({setCount(count-1)}){
                    Text("-")
                }
            }
            repeat(count){
                TextFieldView(it)
            }
        }

    }
}
@Composable
fun TextFieldView(index:Int){
    val (text,setText) = remember { mutableStateOf("") }
    AddWatch("text$index",text)
    TextField(text, onValueChange = {
        setText(it)
    })
}

//        DebagBoardView()
