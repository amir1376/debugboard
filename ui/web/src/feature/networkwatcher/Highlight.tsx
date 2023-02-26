import {useMemo} from "react";
import {run} from "../../utils/functionalUtils";
import hljs from 'highlight.js/lib/common';

export function Highlight(props: {
    text: string
}) {
    const html = useMemo(() => {
        return run(() => {
            try {
                return {
                    __html: hljs.highlightAuto(props.text).value
                }
            } catch (e) {
                return
            }
        })
    }, [props.text])
    return <pre>
        <code
            className="hljs"
            dangerouslySetInnerHTML={html}>

        </code>
    </pre>
}