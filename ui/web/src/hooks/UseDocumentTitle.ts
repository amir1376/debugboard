import {useEffect, useState} from "react";

export function useDocumentTitle() {
    const [title, setTitle] = useState(document.title)
    useEffect(() => {
        const titleDom = document.querySelector("title")!
        const observer = new MutationObserver(mutations => {
            setTitle((mutations[0].target as typeof titleDom).text)
        })
        observer.observe(titleDom, {
            subtree: true,
            characterData: true,
            childList: true,
        })
        return () => observer.disconnect()
    }, [true])
    return title
}