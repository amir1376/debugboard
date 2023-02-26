export type LogLevel =
    | "Info"
    | "Warning"
    | "Debug"
    | "Verbose"
    | "Error"
    | string

export interface LogData {
    tag: string,
    timestamp: number,
    level: LogLevel
    message: string,
}