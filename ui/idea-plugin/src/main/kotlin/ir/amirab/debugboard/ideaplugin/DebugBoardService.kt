package ir.amirab.debugboard.ideaplugin

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project
import io.ktor.client.*
import ir.amirab.debugboard.ideaplugin.session.Session
import ir.amirab.debugboard.ideaplugin.session.SessionListStatusEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@State(name = "DebugBoardConfig")
@Service(Service.Level.PROJECT)
class DebugBoardService(
    val project: Project
) : Disposable, PersistentStateComponent<DebugBoardConfig> {
    var config = DebugBoardConfig()
    val scope = CoroutineScope(SupervisorJob())

    val sessions= mutableListOf(createSession())
    val sessionUpdateFlow= MutableSharedFlow<SessionListStatusEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    fun addSession(){
        val session = createSession()
        sessions.add(session)
        sessionUpdateFlow.tryEmit(SessionListStatusEvent.AddSession(session))
    }
    fun removeSession(session: Session){
        session.close()
        sessions.remove(session)
        sessionUpdateFlow.tryEmit(SessionListStatusEvent.RemoveSession(session))
    }

    init {
        bootstrap()
    }

    private fun createSession(): Session {
        return Session(scope,config,project)
    }

    private fun bootstrap() {

    }

    override fun dispose() {
        scope.cancel()
    }


    override fun getState(): DebugBoardConfig? {
        return config
    }

    override fun loadState(state: DebugBoardConfig) {
        config = state
    }
}