import {BrowserRouter} from "react-router-dom";
import {AppRoutes} from "./router/appRoutes";
import {Layout} from "./layout/Layout";
import {createDrawerState, DrawerContext, DrawerView} from "./feature/drawer/DrawerView";

function App() {
    return (
        <BrowserRouter>
            <Layout>
                <AppRoutes/>
            </Layout>
        </BrowserRouter>
    )
}

export default App
