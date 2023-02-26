import {Backend} from "../service/backend";
import {run, runWith} from "../utils/functionalUtils";
import {env} from "../utils/env";

function createWebsocketLinkFromLocation(path: string = "/") {
    const ws = "ws://"
    const hostSection = window.location.host
    return ws + hostSection + path
}


function getWebsocketPath() {
    // "ws://localhost:8000/"
    let s: string = run(() => {
        if (import.meta.env.DEV) {
            return "ws://localhost:8000/"
        } else {
            return createWebsocketLinkFromLocation();
        }
    })
    console.log("path is ", s)
    return s
}
export const githubUrl="https://github.com/amir1376/debugboard"
export const backend = new Backend(getWebsocketPath())
