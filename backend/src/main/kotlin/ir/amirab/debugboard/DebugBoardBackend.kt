package ir.amirab.debugboard

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import ir.amirab.debugboard.core.DebugBoard
import kotlinx.serialization.json.Json
import ir.amirab.debugboard.handlers.DebugBoardWebSessionHandler
import ir.amirab.debugboard.utils.getLocalIpAddress
import java.time.Duration
import java.util.logging.Logger
import kotlin.concurrent.thread

class DebugBoardBackend(
    private val debugBoard: DebugBoard = DebugBoard.Default,
) {
    private fun Routing.defineDebugBoardRoutes(prefix: String = "/") {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

        route(prefix) {
            install(ContentNegotiation) {
                json(json)
            }
            singlePageApplication {
                useResources = true
                react("web")
            }
            webSocket {
                kotlin.runCatching {
                    DebugBoardWebSessionHandler(
                        debugBoard,
                        json,
                        this,
                    ).handle()
                }.onFailure {
                    it.printStackTrace()
                }.getOrThrow()
            }
        }
    }

    fun startWithDefaultServer(
        port: Int = 8000,
        path: String = "/",
    ) {
        embeddedServer(Netty, port = port, module = {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }
            routing {
                defineDebugBoardRoutes(path)
            }
        }).let {
            thread {
                it.start(wait = false)
            }
        }
        val createLink = { ip: String -> "http://$ip:$port$path" }
        val local = createLink("127.0.0.1")
        val network = getLocalIpAddress().map(createLink)
        val info = """
            DebugBoardServer Started on port $port
            Open DebugBoard Web Panel using 
            - network: $network
            - local $local
            """.trimIndent()
        Logger.getGlobal().info(info)
    }
}

