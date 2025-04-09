import React from "react";
import {useLocation} from "react-router-dom";
import {Spin} from "antd";
import {useHasPermission} from "../../hooks/permissions.tsx";
import Error403 from "../Error403";


const CheckPerms: React.FC<{ children: React.ReactNode }> = (props) => {

    const location = useLocation();
    const {isLoading, hasPermission} = useHasPermission(location.pathname);

    return (
        <>
            {
                isLoading ? <Spin
                        size="large"
                        style={{
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            height: "100%"
                        }}
                    />
                    : (
                        hasPermission() ? props.children : <Error403 />
                    )
            }
        </>
    )
}

export default CheckPerms