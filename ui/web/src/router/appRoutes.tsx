import {Route, Routes} from "react-router-dom";
import {Home} from "../feature/home/Home";
import {VariableWatchPage} from "../feature/varwatcher/VariableWatcher";
import React from "react";
import {NetworkWatcherPage} from "../feature/networkwatcher/NetworkWatcher";
import {LoggerPage} from "../feature/logger/LoggerView";

interface AppRouteInfo {
    path: string,
}

export const appRoutes = {
    home: {
        path: "/"
    },
    varWatch: {
        path: "/varWatch"
    },
    logs: {
        path: "/logs"
    },
    network: {
        path: "/network"
    }
}


export function AppRoutes() {
    return <Routes>
        <Route
            path={appRoutes.home.path}
            element={<Home/>}
        />
        <Route
            path={appRoutes.varWatch.path}
            element={<VariableWatchPage/>}
        />
        <Route
            path={appRoutes.network.path}
            element={<NetworkWatcherPage/>}
        />
        <Route
            path={appRoutes.logs.path}
            element={<LoggerPage/>}
        />
    </Routes>
}