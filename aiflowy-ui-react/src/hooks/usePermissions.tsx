import React, {useContext, useEffect, useState} from "react"
import Axios from "./useAxios.ts";
import {useAppStore} from "../store/appStore.ts";


export interface PermissionsContextType {
    permissions: any[]
}

const PermissionsContext = React.createContext<PermissionsContextType>({permissions:[]})

export const PermissionsProvider: React.FC<{ children: any }> = ({children}) => {

    const [permissions, setPermissions] = useState<any>([])
    const {isLogin} = useAppStore()
    useEffect(() => {
        const fetchConfig = async () => {
            try {
                if (isLogin()) {
                    const res = await Axios.get('/api/v1/auth/getPermissions')
                    setPermissions(res?res.data.data:[]);
                } else {
                    setPermissions([]);
                }
            } catch (error) {
                console.error('获取权限失败', error);
            }
        };

        fetchConfig();
    }, []);

    return (
        <PermissionsContext.Provider value={{permissions}}>{children}</PermissionsContext.Provider>
    )
}

export const usePermissions = (): PermissionsContextType => useContext(PermissionsContext)

export const useCheckPermission = (permissionKey: string) => {
    return usePermissions().permissions.includes(permissionKey)
}