import {bind} from "@react-rxjs/core";
import {backend} from "../../container/Container";
import {Icon} from "@iconify/react";
import {useDocumentTitle} from "../../hooks/UseDocumentTitle";
import {useDrawerState} from "../drawer/DrawerView";
import {isMobile} from "../../utils/responsive/Responsive";
import classNames from "classnames";
import React from "react";
import {useScroll} from "react-use";

export function AppBar() {
    const title = useDocumentTitle()
    const drawerState = useDrawerState()
    const mobile = isMobile()
    return <div className={
        classNames(
            "h-12 flex w-full items-center py-2 bg-base-300",
            "pr-4",
            "shadow",
        )
    }>
        {
            mobile && <div
                className="btn btn-ghost rounded-none"
                onClick={
                    () => drawerState.setIsOpen(true)
                }
            >
                <Icon className="h-6 w-6" icon={"ri:menu-2-fill"}/>
            </div>
        }
        <div className={classNames(
            mobile ? "ml-1" : "ml-4"
        )}/>
        <div className="font-bold flex-grow">
            {title}
        </div>
        <RenderBackendState/>
    </div>
}

const [useBackendState] = bind(backend.wsStatus, "close")

function RenderBackendState() {
    const backendState: "open" | "close" | "error" = useBackendState()
    let content
    switch (backendState) {
        case "open":
            content = <div className="flex items-center text-success">
                <div>
                    Connected
                </div>
            </div>
            break
        default:
            content = <div className="flex space-x-1 items-center text error">
                <div>
                    Connecting
                </div>
                <div className="animate-spin">
                    <Icon icon={"mdi:loading"}/>
                </div>
            </div>
    }
    return content
}

