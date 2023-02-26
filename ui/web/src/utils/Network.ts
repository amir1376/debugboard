import _ from "lodash";
import {AxiosResponse} from "axios";
import {env} from "./env";

const BASE_API_URL = env("VITE_BASE_API_URL")

export function apiPath(path: string): string {
    if (path.startsWith("/")) {
        path = path.substring(1)
    }
    return BASE_API_URL + path
}

export function isHttpCodeSuccess(code: number) {
    return _.inRange(code, 200, 299)
}

export interface ApiResponse<T> {
    response?: T,
    errorResponse?: ErrorResponse,
    code: number,

    onSuccess(block: (result: T) => void): this

    onError(block: (errorResponse: ErrorResponse) => void): this

    isSuccess(): boolean

    mapSuccess<T2>(transform: (src: T) => T2): ApiResponse<T2>
}

class ApiResponseImpl<T> implements ApiResponse<T> {
    response?: T
    errorResponse?: ErrorResponse
    code!: number

    isSuccess(): boolean {
        return isHttpCodeSuccess(this.code)
    }

    onSuccess(block: (result: T) => void): this {
        if (this.isSuccess()) {
            block(this.response!)
        }
        return this
    }

    onError(block: (errorResponse: ErrorResponse) => void): this {
        if (!this.isSuccess()) {
            block(this.errorResponse!)
        }
        return this
    }

    mapSuccess<T2>(transform: (source: T) => T2): ApiResponseImpl<T2> {
        const res = new ApiResponseImpl<T2>();
        if (this.isSuccess()) {
            res.response = transform(this.response!)
        } else {
            res.errorResponse = this.errorResponse
        }
        res.code = this.code
        return res
    }

}

export interface ErrorResponse {
    message: string
}

export function wrap<T>(
    result: AxiosResponse<T | ErrorResponse>
): ApiResponse<T> {
    if (isHttpCodeSuccess(result.status)) {
        return wrapSuccessResult(result.data as T, result.status)
    } else {
        return wrapErrorResult(result.data as ErrorResponse, result.status)
    }
}

export function wrapSuccessResult<T>(result: T, code: number = 200): ApiResponseImpl<T> {
    return _.tap(new ApiResponseImpl(), (response) => {
        response.response = result
        response.code = code
    })
}

export function wrapErrorResult<T>(result: ErrorResponse, code: number = 422): ApiResponseImpl<T> {
    return _.tap(new ApiResponseImpl(), (response) => {
        response.errorResponse = result
        response.code = code
    })
}