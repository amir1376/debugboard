function getViteEnv(key: string) {
    return import.meta.env[key]
}

export function env(key: string) {
    return getViteEnv(key)
}