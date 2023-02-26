import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import './styles/index.css'
import MainInitializer, {noneHeavyInitializers} from "./initializers";
import {addOrReplace} from "./utils/ArrayUtils";

async function setupNonHeavyInitializers() {
    await new MainInitializer()
        .with(noneHeavyInitializers)
        .init()
}

async function mountReact() {
    ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
        <App/>
    )
}

(async () => {
    await setupNonHeavyInitializers()
    await mountReact()
})()