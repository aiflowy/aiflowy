import Axios from 'axios'
import {makeUseAxios} from 'axios-hooks'
import {LRUCache} from "lru-cache";
import {removeUndefinedOrNull} from "../libs/utils.ts";

const baseUrl = `${import.meta.env.VITE_APP_SERVER_ENDPOINT}/`;
const authKey = `${import.meta.env.VITE_APP_AUTH_KEY || "authKey"}`;
const tokenKey = `${import.meta.env.VITE_APP_TOKEN_KEY}`;

const options = {
    max: 500,

    // for use with tracking overall storage size
    // maxSize: 5000,
    maxSize: 100,
    sizeCalculation: (_value: any, _key: any) => {
        return 1
    },

    // for use when you need to clean up something when objects
    // are evicted from the cache
    // dispose: (value, key) => {
    //     freeFromMemoryOrWhatever(value)
    // },

    // how long to live in ms
    ttl: 1000 * 60 * 5,

    // return stale items before removing from cache?
    allowStale: false,

    updateAgeOnGet: false,
    updateAgeOnHas: false,

    // async method to use for cache.fetch(), for
    // stale-while-revalidate type of behavior
    // fetchMethod: async (
    //     key,
    //     staleValue,
    //     { options, signal, context }
    // ) => {},
}


export const axiosCache = new LRUCache(options);

Axios.interceptors.request.use((config) => {
    // config.headers['Authorization'] = localStorage.getItem(authKey);
    config.headers[tokenKey] = localStorage.getItem(authKey);
    config.params = removeUndefinedOrNull(config.params)
    config.baseURL = baseUrl;
    return config
})

export const useAxios =  makeUseAxios({
        cache: axiosCache,
        defaultOptions: {
            useCache: false,
            autoCancel: true,
        },
    })