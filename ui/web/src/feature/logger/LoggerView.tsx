import {FC, useMemo} from "react";
import {observer} from "mobx-react-lite";
import {useViewModel} from "../../base/BaseViewModel";
import {LoggerViewModel} from "./LoggerViewModel";
import {LogData, LogLevel} from "./LogData";
import classNames from "classnames";
import {runWith} from "../../utils/functionalUtils";
import {Helmet} from "react-helmet";
import {Icon} from "@iconify/react";

export function LoggerPage() {
    return <>
        <Helmet>
            <title>
                Logger
            </title>
        </Helmet>
        <LoggerView/>
    </>
}

function LogList(props: { list: ReadonlyArray<LogData> }) {
    if (props.list.length === 0) {
        return <div className="pt-12 text-center text-base-content/80">
            No logs available yet
        </div>
    }

    return <div className="min-w-full w-max pr-4 flex-grow overflow-x-auto">
        {props.list.map((item, index) => (
            <LogItem
                key={index}
                item={item}
            />
        ))}
    </div>
}

function LogFilter(
    props: {
        messageFilter: string,
        tagFilter: string,
        setMessageFilter: (string: string) => void
        setTagFilter: (string: string) => void
    }
) {
    return <div className="bg-base-300 flex items-center space-x-2 shadow p-2">
        <Icon className="w-6 h-6" icon="ic:round-filter-alt"/>
        <input
            value={props.tagFilter}
            onChange={(event) => props.setTagFilter(event.target.value)}
            placeholder="Tag"
            type="text"
            className="input input-bordered w-1/2 md:w-auto input-sm outline-none"
        />
        <input
            value={props.messageFilter}
            onChange={(event) => props.setMessageFilter(event.target.value)}
            placeholder="Filter Message"
            type="text"
            className="input input-bordered w-1/2 md:w-auto input-sm outline-none"
        />
    </div>
}

const LoggerView: FC = observer(() => {
    const vm = useViewModel(() => new LoggerViewModel())
    return <div className="flex flex-col flex-grow overflow-y-hidden">
        <LogFilter
            messageFilter={vm.messageFilter}
            setMessageFilter={(msg) => vm.messageFilter = msg}
            tagFilter={vm.tagFilter}
            setTagFilter={(tag) => vm.tagFilter = tag}
        />
        <div className="flex overflow-y-auto">
            <LogList list={vm.logData}/>
        </div>
    </div>
})

function RenderLogLevel(props: { level: LogLevel }) {
    return <div className={
        classNames(
            "flex-shrink-0 text-opacity-50 font-bold",
            runWith(props.level, (level) => {
                switch (level) {
                    case "Info":
                        return "text-success"
                    case "Debug":
                        return "text-orange-500"
                    case "Error":
                        return "text-error"
                    case "Warning":
                        return "text-warning"
                    default:
                        return "text-base-content"
                }
            })
        )
    }>
        <pre>
            {props.level.padEnd(8, " ")}
        </pre>
    </div>
}

function RenderTag(props: { tag: string }) {
    return <div className="flex-shrink-0 font-bold text-base-content/60">
        <pre>
            {props.tag.padEnd(12, " ")}
        </pre>
    </div>
}

function RenderDate(props: { timestamp: number }) {
    const date = useMemo(() => {
        try {
            const d = new Date(props.timestamp)
            return ("0" + d.getDate()).slice(-2) + "-" + ("0" + (d.getMonth() + 1)).slice(-2) + "-" +
                d.getFullYear() + " " + ("0" + d.getHours()).slice(-2) + ":" + ("0" + d.getMinutes()).slice(-2)
        } catch (e) {
            return "wrong_timestamp"
        }
    }, [props.timestamp])
    return <div className="flex-shrink-0 text-base-content/50">
        {date}
    </div>
}

function RenderMessage(props: { message: string }) {
    return <div className="flex-1">
        <pre>
            {props.message}
        </pre>
    </div>
}

function LogItem(props: { item: LogData }) {
    return <div className="flex space-x-1 hover:bg-base-content/20 transition-colors">
        <RenderDate timestamp={props.item.timestamp}/>
        <RenderTag tag={props.item.tag}/>
        <RenderLogLevel level={props.item.level}/>
        <RenderMessage message={props.item.message}/>
    </div>
}