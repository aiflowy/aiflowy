import {RouterProvider} from "react-router-dom";
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';
import 'antd/dist/reset.css'
import {App as AntdApp, ConfigProvider} from "antd";
import {MittProvider} from "./hooks/useMitt.tsx";
import {router} from "./routers/router.tsx";
import {appConfig} from "./config.tsx";

import "./App.less"

//init i18n
import "./locales/i18n";
import {PermissionsProvider} from "./hooks/usePermissions.tsx";

dayjs.locale('zh');


function App() {
    return (
        <ConfigProvider theme={{
            ...appConfig.theme
        }} locale={appConfig.locale}>
            <MittProvider>
                <PermissionsProvider>
                    <AntdApp style={{fontSize: "medium"}}>
                        <RouterProvider router={router}/>
                    </AntdApp>
                </PermissionsProvider>
            </MittProvider>
        </ConfigProvider>
    )
}

export default App
