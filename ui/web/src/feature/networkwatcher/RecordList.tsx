import {NetworkData, Response, SuccessResponse} from "./NetworkRecord";
import {ClassNameProp, Clickable} from "../../contructs/Components";
import _ from "lodash";
import classNames from "classnames";
import {PropsWithChildren, useMemo} from "react";
import {runWith} from "../../utils/functionalUtils";


export function RecordList(
    props: {
        recordList: ReadonlyArray<NetworkData>,
        selectedItem: NetworkData | null,
        onItemSelected: (item: NetworkData) => void
    }
) {
    if (props.recordList.length === 0) {
        return <div className="pt-12 text-center text-base-content/80">
            No records yet make sure to add network interceptor
        </div>
    } else {
        return <div className="h-full overflow-y-auto flex flex-col space-y-1">
            {
                props.recordList.map(item => {
                    const selected = item === props.selectedItem
                    return <RecordListItem
                        key={item.tag}
                        className={classNames(
                            "transition-colors active:bg-primary active:text-primary-content",
                            selected && "border border-primary",
                            selected ? "bg-primary/30 text-primary-content/70" : "bg-base-200 text-base-content hover:bg-base-content/20 hover:text-base-content",
                        )}
                        networkData={item}
                        onclick={() => {
                            props.onItemSelected(item)
                        }}
                    />
                })
            }
        </div>
    }
}

function CodeWrapper(
    props: {
        color: "success" | "warning" | "error" | "natural"
    } & PropsWithChildren
) {
    const color = props.color
    return <div className={classNames(
        "min-w-[30px] border text-center",
        {
            "text-success border-success": color === "success",
            "text-warning border-warning": color === "warning",
            "text-error border-error": color === "error",
            "text-base-content/80 border-base-content/80": color === "natural",
        }
    )}>
        {props.children}
    </div>
}

function RenderStatus(
    props: { networkResponse: Response | null }
) {
    const response = props.networkResponse;
    if (response === null) {
        return <CodeWrapper color="natural">
            ...
        </CodeWrapper>
    }
    if (response.type === "success") {
        const successResponse = response as SuccessResponse

        let color = runWith(successResponse.code, (code): "success" | "warning" | "error" | "natural" => {
            if (_.inRange(code, 200, 299)) {
                return "success"
            }
            if (_.inRange(code, 300, 399)) {
                return "warning"
            }
            if (_.inRange(code, 400, 599)) {
                return "error"
            }
            return "natural"
        })

        return <CodeWrapper color={color}>
            {successResponse.code}
        </CodeWrapper>
    }
    if (response.type === "fail") {
        return <CodeWrapper color="error">
            !!!
        </CodeWrapper>
    }
    return null
}

export function RecordListItem(props: Partial<Clickable> & ClassNameProp & {
    networkData: NetworkData,
}) {
    const url = useMemo(() => {
        try {
            return new URL(props.networkData.request.url)
        } catch (e) {
            return null
        }
    }, [props.networkData])

    return <div
        className={classNames(
            "flex select-none flex-col cursor-pointer",
            "p-2",
            props.className
        )}
        onClick={props.onclick}
    >
        <div className="flex flex-row space-x-2 items-center overflow-x-hidden">
            <RenderStatus networkResponse={props.networkData.response}/>
            <div className="uppercase font-bold">
                {props.networkData.request.method}
            </div>
            <div className="text-sm text-base-content/75 overflow-hidden whitespace-nowrap overflow-ellipsis">
                {url?.host ?? "<<No Valid URL>>"}
            </div>
        </div>
        <div className="font-bold whitespace-nowrap overflow-hidden overflow-ellipsis">
            {url?.pathname ?? "<<No Valid URL>>"}
        </div>
    </div>
}
