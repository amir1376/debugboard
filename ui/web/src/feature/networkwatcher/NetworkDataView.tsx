import {FailResponse, NetworkData, Request, Response, SuccessResponse} from "./NetworkRecord";
import classNames from "classnames";
import React, {ReactNode, useState} from "react";
import {RecordListItem} from "./RecordList";
import {runWith} from "../../utils/functionalUtils";
import {UnmountClosed} from "react-collapse";
import {Icon} from "@iconify/react";
import {shouldSplitNetworkInfoSections} from "./NetworkWatcher";
import {Highlight} from "./Highlight";

export function NetworkDataView(props: {
    item: NetworkData | null
    clearSelectedItem: () => void
}) {
    if (props.item) {
        const i: NetworkData = props.item as NetworkData
        return <RenderNetworkData clearSelectedItem={props.clearSelectedItem} item={i}/>
    } else {
        return <div className="pt-12 flex items-center justify-center">
            Select a network item to inspect.
        </div>
    }
}

type Tabs = "Request" | "Response"

function RenderTabBar(props: {
    selectedTab: Tabs,
    onTabSelected: (tab: Tabs) => void
}) {
    return <div className="tabs">
        {
            (["Request", "Response"] as Tabs[]).map((name) => (
                <div key={name}
                     className={classNames(
                         "transition-colors tab tab-lg tab-border-3 flex-1 tab-bordered",
                         props.selectedTab == name && "tab-active",
                     )}
                     onClick={() => {
                         props.onTabSelected(name)
                     }}
                >
                    {name}
                </div>
            ))
        }
    </div>;
}


function RenderNetworkData(
    props: {
        item: NetworkData,
        clearSelectedItem: () => void
    }
) {
    const goBack = () => {
        props.clearSelectedItem()
    }
    const networkInfo = props.item
    const [selectedTab, setSelectedTab] = useState<Tabs>("Request")
    let content: ReactNode = runWith(selectedTab, (tab) => {
        if (tab == "Request") {
            return <RenderRequestTab request={networkInfo.request}/>
        } else if (tab == "Response") {
            return <RenderResponseTab response={networkInfo.response}/>
        }
    })

    return <div className="h-full bg-base-200 overflow-y-auto overflow-x-hidden">
        {!shouldSplitNetworkInfoSections() && (
            <div
                onClick={() => goBack()}
                className="flex space-x-2 items-center transition-colors hover:bg-base-content/20">
                <Icon className="w-6 h-6" icon="ic:round-navigate-before"/>
                <RecordListItem networkData={networkInfo}/>
            </div>
        )}
        <RenderTabBar
            selectedTab={selectedTab}
            onTabSelected={(tab) => {
                setSelectedTab(tab)
            }}
        />
        {content}


    </div>
}

function CommonAttributes(props: {
    name: string,
    value?: ReactNode,
}) {
    return <div className="flex flex-row items-center px-2 py-2">
        <div className={classNames(
            "text-base-content/50 font-bold",
            props.value !== undefined ? "w-1/3" : "w-full"
        )}>
            {props.name}
        </div>
        <div className={classNames(
            "text-base-content w-2/3",
            " break-words",
        )}>
            {props.value}
        </div>
    </div>
}

function RenderBody(props: { body: string | null }) {
    const [isOpen, setIsOpen] = useState(true)
    const shortValue = runWith(props.body, b => {
        switch (b) {
            case null:
            case undefined:
                return "null"
            case "":
                return "empty string"
            default:
                return undefined
        }
    })
    return <NetworkInfoCollapse
        header={
            <CommonAttributes
                name="Body"
                value={
                    shortValue && <div className="text-base-content/50">
                        {"<<"}{shortValue}{">>"}
                    </div>
                }
            />
        }
        isOpened={isOpen}
        onHeaderClick={() => setIsOpen(!isOpen)}
        content={
            props.body && <div className="p-2">
                <Highlight text={props.body}/>
            </div>
        }
    />
}

function RenderRequestTab(props: { request: Request }) {
    return <div className="">
        <CommonAttributes
            name="URL"
            value={props.request.url}/>
        <CommonAttributes
            name="Method"
            value={props.request.method}/>
        <RenderHeaders headers={props.request.headers}/>
        <RenderBody body={props.request.body}/>
    </div>
}

function HttpHeaderAndValue(props: { name: string, value: string[] }) {
    return <div className="flex flex-row p-2">
        <div className={classNames(
            "w-1/3 text-base-content/80",
        )}>
            {props.name}
        </div>
        <div className={classNames(
            "w-2/3 text-base-content w-2/3",
        )}>
            {props.value.join("; ")}
        </div>
    </div>
}

function NetworkInfoCollapse(
    props: {
        header: ReactNode,
        isOpened: boolean,
        onHeaderClick: () => void
        content: ReactNode,
    }
) {
    return <div className="flex flex-col">
        <div
            onClick={() => props.onHeaderClick()}
            className="flex items-center select-none cursor-pointer hover:bg-base-content/20 transition-colors">
            <div className="flex-grow">
                {props.header}
            </div>
            <Icon className={classNames(
                "w-6 h-6 transition-transform",
                props.isOpened ? "rotate-0" : "rotate-180"
            )} icon="ic:round-expand-more"/>
        </div>
        <UnmountClosed isOpened={props.isOpened}>
            {props.content}
        </UnmountClosed>
    </div>
}

function RenderHeaders(props: { headers: Record<string, string[]> }) {
    const [isOpened, setOpened] = useState(true)
    return <NetworkInfoCollapse
        isOpened={isOpened}
        onHeaderClick={() => setOpened(!isOpened)}
        header={
            <CommonAttributes name="Headers"/>
        }
        content={
            <div className="flex flex-col">{
                Object.keys(props.headers).map((name) => {
                    return <HttpHeaderAndValue
                        key={name}
                        name={name}
                        value={props.headers[name]}
                    />
                })
            }</div>
        }/>
}

function RenderSuccessResponse(props: { response: SuccessResponse }) {
    const response = props.response
    return <div>
        <CommonAttributes
            name="Code"
            value={response.code}/>
        <CommonAttributes
            name="Description"
            value={response.description}/>
        <RenderHeaders headers={response.headers}/>
        <RenderBody body={response.body}/>
    </div>
}

function RenderResponseTab(props: {
    response: Response | null
}) {
    if (props.response === null) {
        return <div className="flex items-center justify-center">
            No data yet
        </div>
    } else if (props.response.type === "success") {
        return <RenderSuccessResponse response={props.response as SuccessResponse}/>
    } else if (props.response.type === "fail") {
        const failResponse = props.response as FailResponse
        return <div className="flex items-center justify-center">
            {failResponse.cause}
        </div>
    } else {
        return <div className="flex items-center justify-center">
            this type "{props.response.type}" is not supported
        </div>
    }
}