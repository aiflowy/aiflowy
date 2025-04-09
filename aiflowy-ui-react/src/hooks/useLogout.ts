import {useAxios} from "./useAxios";
import {useAppStore} from "../store/appStore";
import {useNavigate} from "react-router-dom";

export const useLogout = ()=>{
    const appStore = useAppStore();
    const navigate = useNavigate();

    const [{loading}, logout] = useAxios({
        url: "/api/v1/auth/logout", method: "post",
    }, {manual: true, useCache: false});

    const doLogout = ()=> {
        logout().then(() => {
            appStore.logOut();
            navigate("/index")
        })
    }

    return {
        loading,
        doLogout
    }
}
