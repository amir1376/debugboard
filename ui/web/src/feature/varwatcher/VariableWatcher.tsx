import React, {createContext, FC, ReactNode, useContext, useEffect, useRef, useState} from "react";
import {VariableInfo} from "./VariableInfo";
import {UnmountClosed} from 'react-collapse';
import {ClassNameProp, Clickable} from "../../contructs/Components";
import {observer} from "mobx-react-lite";
import {useViewModel} from "../../base/BaseViewModel";
import {WarWatcherViewModel} from "./WarWatcherViewModel";
import {Icon} from "@iconify/react";
import classNames from "classnames";
import {Helmet} from "react-helmet";
import {useCopyToClipboard, useHover} from "react-use"
// @ts-ignore
const WarWatcherViewModelContext = createContext<WarWatcherViewModel>(undefined)
export const VariableWatchPage = observer(() => {
    const vm = useViewModel(() => new WarWatcherViewModel())

    return <WarWatcherViewModelContext.Provider value={vm}>
        <Helmet>
            <title>Variable Watcher</title>
        </Helmet>
        <WatcherAppBar/>
        {vm.variableInfo.length === 0 ? (
            <div className="pt-12 text-base-content/75 text-center">
                No Variables added yet.
            </div>
        ) : (
            <VariableWatcher varInfo={vm.variableInfo}/>
        )}
    </WarWatcherViewModelContext.Provider>
})

function WatcherAppBar() {
    return <div className="text-base-content/75 mb-2">
        Watch your variables in runtime and monitor them here
    </div>
}


function RenderChildrenOfVariable(
    props: {
        variableInfo: VariableInfo,
        parentList: ReadonlyArray<string>,
    }
) {
    const vm = useContext(WarWatcherViewModelContext)
    const openedPath = [...props.parentList, props.variableInfo.name]
    useEffect(() => {
        vm.makeOpen(openedPath)
        return () => vm.makeClose(openedPath)
    }, [props.variableInfo.name])
    let content
    if (props.variableInfo.children === null) {
        content = <div>...</div>
    } else {
        const children = props.variableInfo.children as VariableInfo[]
        content = <div className="flex flex-col">
            {
                children.map((c) => <RenderVariable key={c.name}
                                                    variable={c}
                                                    parentList={openedPath}
                />)
            }
        </div>
    }
    return content
}

function RenderVariable(
    props: {
        variable: VariableInfo,
        parentList: string[]
    },
) {
    const [isOpen, setIsOpen] = useState(false)
    return <TreeView
        open={isOpen}
        header={<RenderMainVariable
            isOpen={isOpen}
            onclick={() => setIsOpen(!isOpen)}
            variableInfo={props.variable}/>
        }
        content={
            <RenderChildrenOfVariable
                parentList={props.parentList}
                variableInfo={props.variable}/>
        }
    />
}

function CopyToClipboard(props: ClassNameProp & Clickable) {
    return <div onClick={(e) => {
        e.stopPropagation()
        props.onclick()
    }} className={
        "hover:bg-base-content/20 opacity-50 hover:opacity-100 transition p-1"
    }>
        <Icon icon="material-symbols:content-copy-outline"/>
    </div>
}

export function RenderMainVariable(
    props: {
        isOpen: boolean
        variableInfo: VariableInfo
    } & Clickable
) {
    const [, copyToClipboard] = useCopyToClipboard()
    const Spacer = () => <div className="mx-1"/>
    const info = props.variableInfo
    const content = (hovered) => <div
        className="flex flex-row relative transition-colors hover:bg-base-content/10"
        onClick={props.onclick}
    >
        <Icon
            className={classNames(
                "flex-shrink-0 self-center transition-transform",
                props.isOpen ? "rotate-0" : "-rotate-90"
            )}
            icon="ic:round-expand-more"
        />
        <div className="text-base-content">
            {info.name}
        </div>
        <div className="mx-1">:</div>
        <div className="text-base-content/50">
            {info.type}
        </div>
        <Spacer/>
        <div className={
            classNames(
                "w-min text-success overflow-hidden overflow-ellipsis whitespace-nowrap"
            )
        }>
            {info.value}
        </div>
        <div className={
            classNames(
                "absolute right-0 self-center",
                hovered ? "block" : "hidden"
            )
        }>
            <CopyToClipboard onclick={() => {
                copyToClipboard(props.variableInfo.value)
            }}/>
        </div>
    </div>

    return useHover(content)[0]
}

const VariableWatcher: FC<{
    varInfo: ReadonlyArray<VariableInfo>
}> = (props) => {
    return <div className="select-none bg-base-200 p-1">
        {
            props.varInfo.map(i => {
                return <RenderVariable
                    key={i.name}
                    variable={i}
                    parentList={[]}
                />
            })
        }
    </div>
}


const TreeView: FC<
    {
        open: boolean,
        header
            :
            ReactNode,
        content
            :
            ReactNode,
    }
>
    = (props) => {
    return <div className="flex flex-col">
        {props.header}
        <UnmountClosed isOpened={props.open}>
            <div className="flex">
                <div
                    className="h-auto w-px my-1 mx-1 bg-base-content/30"
                />
                <div className="flex-grow">
                    {props.content}
                </div>
            </div>
        </UnmountClosed>
    </div>

}