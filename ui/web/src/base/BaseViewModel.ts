import {useEffect, useState} from "react";
import {makeObservable} from "mobx";


export abstract class BaseViewModel {
    protected setUp() {
    }

    protected cleanUp() {
    }

    isUp = false

    _start() {
        if (this.isUp) {
            this._stop()
        }
        this.setUp()
        this.isUp = true
    }

    _stop() {
        if (this.isUp) {
            this.cleanUp()
        }
        this.isUp = false
    }
}

export function useViewModel<T extends BaseViewModel>(getViewModel: () => T) {
    const [vm] = useState(getViewModel)
    useEffect(() => {
        vm._start()
        return () => vm._stop()
    }, [])
    return vm
}