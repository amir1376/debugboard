export async function delay(time: number) {
    return new Promise<void>((resolve, reject) => {
        setTimeout(() => {
            resolve()
        }, time)
    })
}