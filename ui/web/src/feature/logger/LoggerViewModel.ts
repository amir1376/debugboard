import {BaseViewModel} from "../../base/BaseViewModel";
import {makeObservable, observable, runInAction} from "mobx";
import {LogData} from "./LogData";
import {backend} from "../../container/Container";
import {Subscription} from "rxjs";

export class LoggerViewModel extends BaseViewModel {
    constructor() {
        super()
        makeObservable(this)
    }

    subscription: Subscription | null = null

    protected setUp() {
        this.subscription = backend.loggerData.subscribe((data) => {
            runInAction(() => {
                this.logData = data
            })
        })
    }

    protected cleanUp() {
        this.subscription?.unsubscribe()
        this.subscription = null
    }


    @observable
    logData: ReadonlyArray<LogData> = []

}