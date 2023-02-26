import {observer} from "mobx-react-lite";
import {Helmet} from "react-helmet"
import React from "react";
import {useDrawerState} from "../drawer/DrawerView";

export const Home: React.FC = observer(() => {
    const {isOpen, setIsOpen} = useDrawerState()
    return <>
        <Helmet>
            <meta charSet="utf-8"/>
            <title>Debug Board</title>
        </Helmet>
        <div className="min-h-screen items-center justify-center flex flex-col container">
            From Drawer Select a section
            <div className="btn " onClick={() => setIsOpen(!isOpen)}>
                {isOpen ? "close" : "open"}
            </div>
        </div>
    </>
})