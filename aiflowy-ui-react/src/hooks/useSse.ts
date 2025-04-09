import {events} from "fetch-event-stream";
import {isBrowser} from "../libs/ssr.ts";
import {useState} from "react";

type StartParams = {
    data: any,
    onMessage: (message: string) => void,
    onError?: (err?: Error) => void,
    onFinished: () => void,
}

const baseUrl = `${import.meta.env.VITE_APP_SERVER_ENDPOINT}/`;
const authKey = `${import.meta.env.VITE_APP_AUTH_KEY || "authKey"}`;
const tokenKey = `${import.meta.env.VITE_APP_TOKEN_KEY}`;

export const useSse = (url: string, headers?: any, options?: any) => {

    const ctrl = new AbortController();
    const [loading, setLoading] = useState(false)

    let sseUrl = url;
    if (sseUrl.startsWith("/")) {
        sseUrl = baseUrl + sseUrl.substring(1);
    }

    const token = isBrowser ? localStorage.getItem(authKey) : null;

    const sseHeaders = {
        Authorization: token || "",
        [tokenKey]: token || "",
        ...headers
    };

    return {
        loading: loading,
        stop: () => {
            ctrl.abort("by stop() invoked!")
            setLoading(false)
        },
        start: async (params: StartParams) => {
            try {
                setLoading(true)
                let res = await fetch(sseUrl, {
                    method: "post",
                    signal: ctrl.signal,
                    headers: sseHeaders,
                    body: JSON.stringify(params.data),
                });

                if (!res.ok) {
                    params.onError?.();
                    return;
                }
                try {
                    let msgEvents = events(res, ctrl.signal);
                    for await (let event of msgEvents) {
                        if (event.data && "[DONE]" !== event.data.trim()) {
                            if (options === 'ollamaInstall'){
                                params.onMessage(event.data)
                            } else {
                                let temp = JSON.parse(event.data);
                                params.onMessage(temp.content)
                            }

                        }
                    }
                } catch (err) {
                    console.error("error", err);
                    params.onError?.()
                } finally {
                    params.onFinished();
                }
            } finally {
                setLoading(false)
            }

        }
    }
}
