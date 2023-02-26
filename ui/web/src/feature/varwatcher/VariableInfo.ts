export interface VariableInfo {
    name: string
    type: string
    value: string
    children: VariableInfo[] | undefined
}
