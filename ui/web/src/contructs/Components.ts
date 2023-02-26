import {Key} from "react";

export type ClassNameProp = {
    className?: string
}

export type Clickable = {
    onclick: () => void
}

export type TransitionProps = {
    in?: boolean
    key?: Key
}