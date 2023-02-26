import {BaseViewModel} from "../../base/BaseViewModel";
import {action, computed, makeObservable, observable, runInAction, toJS} from "mobx";
import {VariableInfo} from "./VariableInfo";
import {fakeInfo} from "./FakeInfo";
import _, {iteratee} from "lodash";
import {watch} from "vite/types/chokidar";
import {Backend} from "../../service/backend";
import {BehaviorSubject, Unsubscribable} from "rxjs";
import {backend} from "../../container/Container";


export class WarWatcherViewModel extends BaseViewModel {
    constructor() {
        super()
        makeObservable(this)
    }

    varSubscription: Unsubscribable | null = null

    protected setUp() {
        this.varSubscription = backend.variableWatchData.subscribe((value) => {
            runInAction(
                () => {
                    this.variableInfo = value
                }
            )
        })
    }

    protected cleanUp() {
        this.varSubscription?.unsubscribe()
    }

    updateServerPath = _.debounce(async () => {
        backend.updateVarWatchPath(toJS(this.openedPaths))
    }, 100)

    @observable
    openedPaths = new Array<string[]>()


    @action
    makeOpen(path: string[]) {
        console.log("opened ", path)
        const found = this.openedPaths.find((item) => {
            return _.isEqual(path, item)
        })
        if (!found) {
            this.openedPaths.push(path)
        }
        this.updateServerPath()
    }

    @action
    makeClose(path: string[]) {
        console.log("closed ", path)
        _.remove(this.openedPaths, (item) => {
            const result = _.isEqual(item, path)
            console.log(item, path, result)
            return result
        })
        this.updateServerPath()
    }

    @observable
    variableInfo: ReadonlyArray<VariableInfo> = []
}