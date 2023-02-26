import React, {createContext, FC, PropsWithChildren, ReactNode, useContext, useState} from "react";
import {isDesktop, isMobile} from "../../utils/responsive/Responsive";

import {Icon} from "@iconify/react";
import classNames from "classnames";
import {Link, useLocation} from "react-router-dom";
import {ClassNameProp} from "../../contructs/Components";
import {appRoutes} from "../../router/appRoutes";
import {githubUrl} from "../../container/Container";

interface DrawerState {
    isOpen: boolean,

    setIsOpen(isOpen: boolean)
}

export function createDrawerState() {
    const [isOpen, setIsOpen] = useState(false)
    return {
        isOpen,
        setIsOpen,
    } as DrawerState
}

// @ts-ignore
export const DrawerContext = createContext<DrawerState>(undefined)

export function useDrawerState() {
    return useContext(DrawerContext)
}

function shouldMakeDrawerSticky() {
    return !isMobile()
}

export const DrawerView: FC<PropsWithChildren> = (props) => {
    const stickyDrawer = shouldMakeDrawerSticky()
    const state = useDrawerState()
    let content
    if (stickyDrawer) {
        content = <div className="min-h-screen max-h-screen flex flex-row">
            <DrawerContent/>
            <div className="overflow-x-hidden overflow-y-scroll flex-grow">
                {props.children}
            </div>
        </div>
    } else {
        content = <Drawer
            drawerState={state}
            sideBarContent={
                <DrawerContent/>
            }
            mainContent={props.children}
        />

    }
    return content
}

export function Drawer(props: {
    drawerState: DrawerState,
    sideBarContent: ReactNode,
    mainContent: ReactNode,
}) {
    return <div className="drawer">
        <input
            checked={props.drawerState.isOpen}
            onChange={(event) => props.drawerState.setIsOpen(event.target.checked)}
            id="my-drawer"
            type="checkbox"
            className="drawer-toggle"
        />
        <div className="drawer-content">
            {props.mainContent}
        </div>
        <div className="drawer-side">
            <label htmlFor="my-drawer" className="drawer-overlay"></label>
            {props.sideBarContent}
        </div>
    </div>
}

function RenderSideMenu() {
    const path = useLocation().pathname

    return <ul className="menu text-base-content">
        <DrawerItem
            link={appRoutes.varWatch.path}
            title="Watcher"
            selected={path === appRoutes.varWatch.path}
            icon="material-symbols:format-list-bulleted-sharp"
        />
        <DrawerItem
            link={appRoutes.logs.path}
            title="Log"
            selected={path === appRoutes.logs.path}
            icon="material-symbols:format-list-bulleted-sharp"
        />
        <DrawerItem
            link={appRoutes.network.path}
            title="Network"
            selected={path === appRoutes.network.path}
            icon="mdi:internet"
        />
    </ul>
}

function RenderSideFooter() {
    return <ul className="menu">
        <li>
            <div className="p-2 flex">
                <Icon icon="mdi:github"/>
                <a href={githubUrl}>
                    Github
                </a>
            </div>
        </li>
    </ul>
}

export function DrawerDivider() {
    return <div className="h-px bg-gradient-to-r from-base-content/10 via-base-content/30 from-base-content/10"/>
}

export function DrawerContent() {
    const stickyDrawer = shouldMakeDrawerSticky()
    return <div className={classNames(
        "flex flex-shrink-0 flex-col w-72 bg-base-100",
        stickyDrawer && "border-r border-base-content/20"
    )}>
        <RenderSideHeader/>
        <DrawerDivider/>
        <RenderSideMenu/>
        <div className="flex-grow"/>
        <DrawerDivider/>
        <RenderSideFooter/>
    </div>
}

function RenderSideHeader() {
    return <div className="p-4 flex w-full justify-center">
        <Logo className="w-12 h-12"/>
        <div className="flex flex-col">
            <h1 className="font-bold text-lg">Debug Board</h1>
            <div className="opacity-50 text-sm">Web panel</div>
        </div>
    </div>
}

function Logo(
    props: ClassNameProp
) {
    return <svg {...props} width="100" height="100" viewBox="0 0 234 235" fill="none"
                xmlns="http://www.w3.org/2000/svg">
        <path fillRule="evenodd" clipRule="evenodd"
              d="M154 36C154 48.6714 147.362 59.8137 137.329 66.2292C139.964 67.1511 142.66 68.3069 145.279 69.7557C157.555 76.548 166.924 87.5848 171.624 100.791H184.438C186.974 100.791 189.406 101.797 191.199 103.588C192.993 105.379 194 107.808 194 110.34C194 112.873 192.993 115.302 191.199 117.093C189.406 118.883 186.974 119.89 184.438 119.89H174.875V138.988H184.438C186.974 138.988 189.406 139.994 191.199 141.785C192.993 143.576 194 146.005 194 148.537C194 151.07 192.993 153.499 191.199 155.29C189.406 157.081 186.974 158.087 184.438 158.087H174.875V177.185H184.438C186.974 177.185 189.406 178.191 191.199 179.982C192.993 181.773 194 184.202 194 186.735C194 189.267 192.993 191.696 191.199 193.487C189.406 195.278 186.974 196.284 184.438 196.284H171.614C167.657 207.459 160.327 217.134 150.635 223.977C140.943 230.821 129.365 234.495 117.495 234.495C105.625 234.495 94.0473 230.821 84.3553 223.977C74.6633 217.134 67.3339 207.459 63.3763 196.284H50.5625C48.0264 196.284 45.5941 195.278 43.8008 193.487C42.0075 191.696 41 189.267 41 186.735C41 184.202 42.0075 181.773 43.8008 179.982C45.5941 178.191 48.0264 177.185 50.5625 177.185H60.125V158.087H50.5625C48.0264 158.087 45.5941 157.081 43.8008 155.29C42.0075 153.499 41 151.07 41 148.537C41 146.005 42.0075 143.576 43.8008 141.785C45.5941 139.994 48.0264 138.988 50.5625 138.988H60.125V119.89H50.5625C48.0264 119.89 45.5941 118.883 43.8008 117.093C42.0075 115.302 41 112.873 41 110.34C41 107.808 42.0075 105.379 43.8008 103.588C45.5941 101.797 48.0264 100.791 50.5625 100.791H63.3858C68.0806 87.5879 77.4422 76.5515 89.7114 69.7557C92.3302 68.3052 95.0281 67.1485 97.6656 66.226C87.6354 59.8101 81 48.6693 81 36C81 16.1177 97.3416 0 117.5 0C137.658 0 154 16.1177 154 36ZM117.5 62C132.266 62 144 50.2298 144 36C144 21.7702 132.266 10 117.5 10C102.734 10 91 21.7702 91 36C91 50.2298 102.734 62 117.5 62ZM79.25 119.89V177.185C79.25 187.316 83.2799 197.031 90.4532 204.195C97.6264 211.358 107.355 215.382 117.5 215.382C127.645 215.382 137.374 211.358 144.547 204.195C151.72 197.031 155.75 187.316 155.75 177.185V119.89C155.75 109.759 151.72 100.043 144.547 92.8801C137.374 85.7167 127.645 81.6924 117.5 81.6924C107.355 81.6924 97.6264 85.7167 90.4532 92.8801C83.2799 100.043 79.25 109.759 79.25 119.89ZM116 28C116 29.1046 116.895 30 118 30C119.105 30 120 29.1046 120 28V21C120 19.8954 119.105 19 118 19C116.895 19 116 19.8954 116 21V28ZM100 36C100 34.8954 100.895 34 102 34H109C110.105 34 111 34.8954 111 36C111 37.1046 110.105 38 109 38H102C100.895 38 100 37.1046 100 36ZM116 50.0452C116 51.1498 116.895 52.0452 118 52.0452C119.105 52.0452 120 51.1498 120 50.0452V43.0452C120 41.9406 119.105 41.0452 118 41.0452C116.895 41.0452 116 41.9406 116 43.0452V50.0452ZM126 34C124.895 34 124 34.8954 124 36C124 37.1046 124.895 38 126 38H133C134.105 38 135 37.1046 135 36C135 34.8954 134.105 34 133 34H126ZM94 134.5C94 129.253 98.2533 125 103.5 125H132.5C137.747 125 142 129.253 142 134.5C142 139.747 137.747 144 132.5 144H103.5C98.2533 144 94 139.747 94 134.5ZM94 162.5C94 157.253 98.2533 153 103.5 153H132.5C137.747 153 142 157.253 142 162.5C142 167.747 137.747 172 132.5 172H103.5C98.2533 172 94 167.747 94 162.5Z"
              fill="url(#paint0_linear_12_178)"/>
        <defs>
            <linearGradient id="paint0_linear_12_178" x1="194" y1="-5.95017e-06" x2="41" y2="234"
                            gradientUnits="userSpaceOnUse">
                <stop stopColor="#3B82F6"/>
                <stop offset="1" stopColor="#10B981"/>
            </linearGradient>
        </defs>
    </svg>

}

function DrawerItem2(
    props: {
        title: string,
        selected: boolean,
        icon: string,
    }
) {
    return <div>
        <div className={classNames(
            "flex flex-row items-center",
            "space-x-2",
            "p-2",
            "transition-colors",
            "hover:bg-base-content/20",
            props.selected ? "bg-base-content/20" : ""
        )}>
            <Icon icon={props.icon}/>
            <div>
                {props.title}
            </div>
        </div>
    </div>
}

function DrawerItem(
    props: {
        title: string,
        selected: boolean,
        icon: string,
        link?: string,
    }
) {
    const drawerState = useDrawerState()
    return <li>
        <Link onClick={() => {
            drawerState.setIsOpen(false)
        }
        } to={props.link ?? ""} className={classNames({
            "active": props.selected
        })}>
            <Icon icon={props.icon}/>
            <div>
                {props.title}
            </div>
        </Link>
    </li>
}
