export function runWith<T, R>(
    value: T,
    block: (value: T) => R
): R {
    return block(value)
}

export function run<R>(block: () => R) {
    return block()
}