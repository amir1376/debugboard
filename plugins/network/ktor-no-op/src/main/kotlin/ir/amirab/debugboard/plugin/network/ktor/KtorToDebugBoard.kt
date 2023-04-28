package ir.amirab.debugboard.plugin.network.ktor

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.util.*
import ir.amirab.debugboard.core.DebugBoard
class KtorDebugBoard private constructor() {
    companion object : HttpClientPlugin<Config, KtorDebugBoard> {
        override val key: AttributeKey<KtorDebugBoard> = AttributeKey("DebugBoard")

        override fun prepare(block: Config.() -> Unit): KtorDebugBoard {
            return KtorDebugBoard()
        }

        override fun install(plugin: KtorDebugBoard, scope: HttpClient) {}
    }
}

@KtorDsl
class Config internal constructor() {
    var debugBoard: DebugBoard = DebugBoard.Default
}
