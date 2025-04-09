import {create} from 'zustand'
import {createJSONStorage, persist} from "zustand/middleware";

const authKey = `${import.meta.env.VITE_APP_AUTH_KEY || "authKey"}`;

export interface AppStore {
    token?: string,
    setToken: (token: string) => void,
    nickName?: string,
    avatar?: string,
    jwt?: string,
    isLogin: () => boolean,
    setLogin: (jwt: string, nickName: string) => void,
    setNickname: (nickName: string) => void,
    setAvatar: (avatar: string) => void,
    setJwt: (jwt: string) => void,
    logOut: () => void,

}

export const useAppStore = create<AppStore>()(persist(
    (set, get) => ({
        nickName: '',
        avatar: '',
        jwt: '',
        token: '',
        setToken: (token: string) => {
            localStorage.setItem(authKey, token)
            set({token: token})
        },
        isLogin: () => {
            // const token = get().jwt;
            const token = get().token;
            if (!token) {
                return false;
            }
            // const tokenParts = token.split('.');
            // if (tokenParts.length != 3) {
            //     return false;
            // }
            // const payload = tokenParts[1];
            // try {
            //     const {exp} = JSON.parse(window.atob(payload))
            //     return Date.now() < exp * 1000;
            // } catch (e) {
            //     return false;
            // }
            return true;
        },
        setLogin: (jwt: string, nickName: string) => {
            localStorage.setItem(authKey, jwt)
            set({nickName: nickName, jwt: jwt})
        },
        setNickname: (nickName: string) => {
            set({nickName: nickName})
        },
        setAvatar: (avatar: string) => {
            set({avatar: avatar})
        },
        setJwt: (jwt: string) => {
            localStorage.setItem(authKey, jwt)
            set({jwt: jwt})
        },
        logOut: () => {
            localStorage.removeItem(authKey)
            set({nickName: '', avatar: '', jwt: ''})
            set({token: ''})

        },
    }), {
        name: "aiadmin-store",
        storage: createJSONStorage(() => localStorage)
    })
)