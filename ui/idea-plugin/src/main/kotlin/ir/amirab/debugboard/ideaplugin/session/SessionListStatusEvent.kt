package ir.amirab.debugboard.ideaplugin.session

sealed class SessionListStatusEvent {
    data class RemoveSession(
        val session: Session
    ):SessionListStatusEvent()
    data class AddSession(val session: Session):SessionListStatusEvent()

}