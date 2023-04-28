package ir.amirab.debugboard.backend

import ir.amirab.debugboard.core.DebugBoard

class DebugBoardBackend(
    debugBoard:DebugBoard=DebugBoard.Default
) : IDebugBoardBackend{
    override fun startWithDefaultServer(port: Int, path: String) {
        // No Operation
    }
}