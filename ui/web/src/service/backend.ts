import {BehaviorSubject, filter, Subject, Subscription} from "rxjs"
import {VariableInfo} from "../feature/varwatcher/VariableInfo";
import {isString} from "lodash";
import {NetworkData} from "../feature/networkwatcher/NetworkRecord";
import {LogData} from "../feature/logger/LogData";
import {addOrReplace} from "../utils/ArrayUtils";

export class Backend {

    private readonly path: string
    private ws!: RetryOnFailWebsocket


    readonly variableWatchData: Subject<ReadonlyArray<VariableInfo>>
    readonly loggerData: BehaviorSubject<ReadonlyArray<LogData>>
    readonly networkData: BehaviorSubject<ReadonlyArray<NetworkData>>


    onConnectSubscription: Subscription | null = null

    constructor(path: string) {
        this.path = path
        this.variableWatchData = new BehaviorSubject<ReadonlyArray<VariableInfo>>([])
        this.loggerData = new BehaviorSubject<ReadonlyArray<LogData>>([])
        this.networkData = new BehaviorSubject<ReadonlyArray<NetworkData>>([])
        console.log("initiating")
        this.onConnectSubscription = this.wsStatus
            .pipe(
                filter(s => s === "open")
            ).subscribe(() => {
                this.subscribeToLogs(true)
                this.subscribeToNetwork(true)
            })
        this.createWebsocket()
    }

    close() {
        this.onConnectSubscription?.unsubscribe()
        this.onConnectSubscription = null
    }


    wsStatus = new BehaviorSubject<WebsocketState>("close")

    private createWebsocket() {
        this.ws = new RetryOnFailWebsocket(
            this.path,
            (ev) => {
                this.handleIncomingMessage(ev.data)
            },
            this.wsStatus
        )
    }

    dispose() {
        this.ws?.close()
    }


    updateVarWatchPath(
        openedPaths: Array<string[]>
    ) {
        const list = openedPaths
        // const list=Array.from(openedPaths)
        console.log("send new paths ", list)
        this.ws?.send(JSON.stringify({
            type: "VariableWatcher.setOpenPaths",
            paths: list
        }))
    }

    subscribeToLogs(subscribe: boolean) {
        this.ws.send(JSON.stringify({
            type: `Logger.${subscribe ? "" : "un"}subscribe`
        }))
    }

    subscribeToNetwork(subscribe: boolean) {
        this.ws.send(JSON.stringify({
            type: `Network.${subscribe ? "" : "un"}subscribe`
        }))
    }

    private handleIncomingMessage(data: any) {
        if (!isString(data)) {
            console.log("data received is not string!", {data})
            return
        }
        let jsonData: any
        try {
            jsonData = JSON.parse(data)
        } catch (e) {
            console.log("data received is not json!", {data})
            return;
        }
        console.log(jsonData.type)
        switch (jsonData.type) {
            case "VariableWatcher.onNewData":
                this.onNewVariableWatcherData(jsonData)
                break;
            case "NetworkWatcher.onNewData":
                this.onNewNetworkWatcherData(jsonData)
                break;
            case "Logger.onNewData":
                this.onNewLoggerData(jsonData)
                break;
            case "message":
                console.log(jsonData.value)
                break;
            default:
                console.log(jsonData, jsonData.type, "cant be handled!")
                break;
        }
    }

    private onNewVariableWatcherData(jsonData) {
        this.variableWatchData.next(jsonData.value)
    }

    private onNewNetworkWatcherData(jsonData: UpdatableData<NetworkData[]>) {
        let newList: ReadonlyArray<NetworkData>
        if (jsonData.isThisInitialValue) {
            newList = jsonData.value
        } else {
            newList = this.networkData.value

            for (const newItem of jsonData.value) {
                newList = addOrReplace(
                    newList,
                    newItem,
                    nd => nd.tag
                )
            }
        }
        this.networkData.next(newList)
    }

    private onNewLoggerData(jsonData: UpdatableData<LogData[]>) {
        console.log("new data", jsonData)
        if (jsonData.isThisInitialValue) {
            this.loggerData.next(jsonData.value)
        } else {
            const newData = [
                ...this.loggerData.value,
                ...jsonData.value
            ];
            this.loggerData.next(newData)
        }
    }
}


export type WebsocketState =
    | "open"
    | "close"
    | "error"

class RetryOnFailWebsocket {
    constructor(
        private path: string,
        private handler: (data: MessageEvent) => void,
        private readonly wsSubject: BehaviorSubject<WebsocketState>
    ) {
        this.connect()
    }

    private ws: WebSocket | null = null


    close() {
        this.ws?.close()
        this.ws = null
    }

    private connect = () => {
        this.ws = new WebSocket(this.path)
        this.ws.addEventListener("open", () => {
            console.log("Websocket is now open.")
            this.wsSubject.next("open")
        })
        this.ws.addEventListener("close", () => {
            console.log("Websocket closed.")
            this.wsSubject.next("close")
        })
        this.ws.addEventListener("error", () => {
            console.log("Websocket has error!")
            this.wsSubject.next("error")
            this.reconnect()
        })
        this.ws.addEventListener("message", (ev) => {
            this.handler(ev)
        })
    }
    send = (data: string | ArrayBufferLike | Blob | ArrayBufferView) => {
        if (this.ws?.readyState === WebSocket.OPEN) {
            this.ws.send(data)
            return true
        }
        return false
    }

    private reconnect = () => {
        setTimeout(() => {
            this.connect()
        }, 1000)
    }
}

interface UpdatableData<T> {
    isThisInitialValue: boolean
    value: T
}