import {VariableInfo} from "./VariableInfo";

export const fakeInfo: VariableInfo[] = [
    {
        name: "a",
        type: "String",
        value: "123",
        children: [
            {
                name: "length",
                type: "Int",
                value: "3",
                children: [],
            }
        ]
    },
    {
        name: "myList",
        type: "ArrayList",
        value: "[1,2,3]",
        children: [
            {
                name: "[0]",
                type: "Int",
                value: "1",
                children: []
            },
            {
                name: "[1]",
                type: "Int",
                value: "2",
                children: []
            },
            {
                name: "[2]",
                type: "Int",
                value: "3",
                children: []
            }
        ]
    }
]