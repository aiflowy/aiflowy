import {useAxios} from "./useAxios.ts";

export const useLogin = ()=>{
    const [{loading}, doLogin] = useAxios({
        url: "/api/v1/account/login", method: "post",
    }, {manual: true, useCache: false});

    return {
        loading,
        doLogin
    }
}

export const useLoginV3 = ()=> {
    const [{loading}, doLogin] = useAxios({
        url: "/api/v1/auth/login", method: "post",
    }, {manual: true, useCache: false});

    return {
        loading,
        doLogin
    }
}