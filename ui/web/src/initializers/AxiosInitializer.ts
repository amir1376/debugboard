import axios from "axios";
import {Initializer} from "../contructs/Initializer";

export default class AxiosInitializer implements Initializer {
    async init() {
        axios.defaults.withCredentials = true
        axios.defaults.validateStatus = status => {
            return status < 500
        }
        axios.defaults.headers.common["Accept"] = "application/json"
    }
}