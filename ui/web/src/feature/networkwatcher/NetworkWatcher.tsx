import {useViewModel} from "../../base/BaseViewModel";
import {NetworkWatcherViewModel} from "./NetworkWatcherViewModel";
import React from "react";
import {observer} from "mobx-react-lite";
import {NetworkDataView} from "./NetworkDataView";
import {RecordList} from "./RecordList";
import {isDesktop} from "../../utils/responsive/Responsive";
import {Helmet} from "react-helmet";

export function shouldSplitNetworkInfoSections() {
    return isDesktop()
}

const NetworkWatcherView = observer(
    () => {
        const vm = useViewModel(() => new NetworkWatcherViewModel)
        if (!shouldSplitNetworkInfoSections()) {
            if (vm.selectedItem) {
                return <NetworkDataView
                    item={vm.selectedItem}
                    clearSelectedItem={() => vm.selectedItem = null}
                />
            } else {
                return <RecordList
                    selectedItem={vm.selectedItem}
                    recordList={vm.list}
                    onItemSelected={(i) => vm.selectedItem = i}
                />
            }
        } else {
            return <div className="h-full flex overflow-y-hidden">
                <div className="w-1/3 pr-1">
                    <RecordList
                        selectedItem={vm.selectedItem}
                        recordList={vm.list}
                        onItemSelected={(i) => vm.selectedItem = i}
                    />
                </div>
                <div className="w-2/3 pl-1">
                    <NetworkDataView
                        item={vm.selectedItem}
                        clearSelectedItem={() => vm.selectedItem = null}
                    />
                </div>
            </div>
        }
    }
);

export function NetworkWatcherPage() {
    return <>
        <Helmet>
            <title>
                Network Watcher
            </title>
        </Helmet>
        <NetworkWatcherView/>
    </>
}

