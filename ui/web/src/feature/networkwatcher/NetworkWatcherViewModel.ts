import {BaseViewModel} from "../../base/BaseViewModel";
import {computed, makeObservable, observable, runInAction} from "mobx";
import {NetworkData} from "./NetworkRecord";
import {backend} from "../../container/Container";
import {Subscription} from "rxjs";

export class NetworkWatcherViewModel extends BaseViewModel {
    constructor() {
        super()
        makeObservable(this)
    }


    subscription: Subscription | null = null

    protected setUp() {
        super.setUp();
        this.subscription = backend.networkData.subscribe((d) => {
            runInAction(() => {
                this.list = d
            })
        })
    }

    protected cleanUp() {
        super.cleanUp();
        this.subscription?.unsubscribe()
        this.subscription = null
    }

    @observable
    public list: ReadonlyArray<NetworkData> = []

    @observable
    _selectedItem: string | null = null
    @computed
    get selectedItem(): NetworkData | null {
        const networkData = this.list.find(i => i.tag === this._selectedItem);
        if (!networkData) {
            return null
        }
        return networkData
        // return this.list.find(i => i.tag === this._selectedItem) || null
    }

    set selectedItem(item: NetworkData | null) {
        this._selectedItem = item?.tag ?? null
    }
}
