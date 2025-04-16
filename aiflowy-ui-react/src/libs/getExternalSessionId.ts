import {uuid} from "./uuid.ts";

const sessionIdKey = "__aibot_msg_external_sessionid";

export const getExternalSessionId = () => {
    let sessionId = localStorage.getItem(sessionIdKey)
    if (!sessionId) {
        sessionId = uuid();
        localStorage.setItem(sessionIdKey, sessionId)
    }
    return sessionId;
}


export const resetExternalSessionId = () => {
    localStorage.removeItem(sessionIdKey)
}

export const updateExternalSessionId = (sessionId: string) => {
    localStorage.setItem(sessionIdKey, sessionId)
}

export const setNewExternalSessionId = () => {
    let sessionId = uuid();
    localStorage.setItem(sessionIdKey, sessionId)
}

