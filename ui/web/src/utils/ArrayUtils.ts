import produce, {Draft} from "immer"
import {WritableDraft} from "immer/dist/types/types-external";

export function arrayJoin<T>(
    array: Array<T>,
    delimiter: (
        array: Array<T>,
        before: T,
        next: T,
    ) => T
) {
    const out: T[] = []
    for (let i = 0; i < array.length; i++) {
        out.push(array[i])
        if (i !== array.length - 1) {
            out.push(delimiter(array, array[i], array[i + 1]))
        }
    }
    return out
}

export function addOrReplace<T>(
    list: ReadonlyArray<T>, item: T, selector: (item: T) => any
): ReadonlyArray<T> {
    const itemSelector = selector(item)
    const index = list.findIndex(i => {
        return selector(i) === itemSelector
    })

    if (index === -1) {
        return produce(list, (draft) => {
            // @ts-ignore
            draft.push(item)
        })
    } else {
        return produce(list, draft => {
            // @ts-ignore
            draft[index] = item
        })
    }
}

const baseState: { title: string, done: boolean }[] = [
    {
        title: "Learn TypeScript",
        done: true
    },
    {
        title: "Try Immer",
        done: false
    }
]