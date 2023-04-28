package ir.amirab.debugboard.backend

interface IDebugBoardBackend {
    fun startWithDefaultServer(port: Int = 8000, path: String = "/", )
}