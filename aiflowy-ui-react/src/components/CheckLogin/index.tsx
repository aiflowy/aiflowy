import React from "react";
import {Navigate} from "react-router-dom";
import {useAppStore} from "../../store/appStore.ts";


const CheckLogin: React.FC<{ children: React.ReactNode }> = (props) => {
    const isLogin = useAppStore((info)=>info.isLogin());
    return (
        <>
            {isLogin ? props.children
                : <Navigate to={"/login"} replace={true}/>}
        </>
    )
}

export default CheckLogin