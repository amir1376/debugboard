import {BaseViewModel} from "../../base/BaseViewModel";
import {computed, makeObservable, observable, runInAction} from "mobx";
import {LogData} from "./LogData";
import {backend} from "../../container/Container";
import {Subscription} from "rxjs";
import _ from "lodash";

export class LoggerViewModel extends BaseViewModel {

    constructor() {
        super()
        makeObservable(this)
    }

    subscription: Subscription | null = null

    protected setUp() {
        this.subscription = backend.loggerData.subscribe((data) => {
            runInAction(() => {
                this._logData = data
            })
        })
    }

    protected cleanUp() {
        this.subscription?.unsubscribe()
        this.subscription = null
    }


    @observable
    private _logData: ReadonlyArray<LogData> = []
    @computed
    get logData() {
        const messageEmpty = _.isEmpty(this.messageFilter);
        const tagEmpty = _.isEmpty(this.tagFilter);
        if (messageEmpty && tagEmpty) {
            return this._logData
        }
        return this._logData.filter((item) => {
            if (!tagEmpty && !item.tag.includes(this.tagFilter)) {
                return false
            }
            if (!messageEmpty && !item.message.includes(this.messageFilter)) {
                return false
            }

            return true
        })
    }

    @observable
    private _messageFilter: string = ""
    @observable
    private _tagFilter: string = ""

    @computed
    get messageFilter() {
        return this._messageFilter
    }

    set messageFilter(msg) {
        this._messageFilter = msg
    }

    @computed
    get tagFilter() {
        return this._tagFilter
    }

    set tagFilter(tag) {
        this._tagFilter = tag
    }


}