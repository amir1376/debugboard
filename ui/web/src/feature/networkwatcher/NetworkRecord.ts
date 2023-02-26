export interface Request {
    headers: Record<string, string[]>
    method: string //get put post option
    url: string
    body: string | null
}

export interface Response {
    type: "fail" | "success"
}

export interface FailResponse extends Response {
    type: "fail"
    cause: string
}

export interface SuccessResponse extends Response {
    type: "success"
    code: number
    description: string
    headers: Record<string, string[]>
    body: string | null,
}

export interface NetworkData {
    tag: string
    request: Request
    response: Response | null
}