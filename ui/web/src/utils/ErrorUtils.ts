export function getErrorMessage(e: unknown, fallback: string): string {
    if (typeof e === "string") {
        return e
    } else if (typeof e === "object") {
        let error = e as { message?: any }
        if (typeof error.message === "string") {
            return error.message
        }
    }
    return fallback
}