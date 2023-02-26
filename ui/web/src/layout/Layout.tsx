import React, {PropsWithChildren, useMemo} from "react";
import {createDrawerState, DrawerContext, DrawerView, useDrawerState} from "../feature/drawer/DrawerView";
import {AppBar} from "../feature/appbar/AppBar";

function RegisterProviders(
    props: PropsWithChildren
) {
    const drawerState = createDrawerState()
    return <DrawerContext.Provider value={drawerState}>
        {props.children}
    </DrawerContext.Provider>
}

export function Layout({children}: PropsWithChildren) {
    return <RegisterProviders>
        <DrawerView>
            <div className="min-h-screen max-h-screen flex flex-col">
                <AppBar/>
                <div className="flex flex-col flex-grow px-2 sm:px-4 pt-2 overflow-y-auto">
                    {children}
                </div>
            </div>
        </DrawerView>
    </RegisterProviders>
}