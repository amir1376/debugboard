import AxiosInitializer from "./AxiosInitializer";
import {Initializer} from "../contructs/Initializer";

export const noneHeavyInitializers = [
    //define initializers here
    AxiosInitializer,
]
export const heavyInitializer = [
    //define initializers here
]

class MainInitializer implements Initializer {
    initializersInstances: Initializer[] = []
    initialized = false
    private initializers: Array<new() => Initializer> = []

    with(initializers: Array<new() => Initializer>) {
        this.initializers = initializers
        return this
    }

    dispose() {
        for (const i of this.initializersInstances) {
            i.dispose?.()
        }
        this.initializersInstances = []
    }

    async init() {
        if (this.initialized) throw Error("duplicate initialization")
        for (let initializer of this.initializers) {
            const initializerInstance = new initializer;
            this.initializersInstances.push(initializerInstance)
            try {
                await initializerInstance.init()
            } catch (error) {
                this.dispose()
                throw error
            }
        }
        this.initialized = true
    }
}

export default MainInitializer
