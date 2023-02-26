export function isWebLink(uri: string) {
    return uri.startsWith("http://") || uri.startsWith("https://")
}