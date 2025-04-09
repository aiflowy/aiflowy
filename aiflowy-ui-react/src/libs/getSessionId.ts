import {uuid} from "./uuid.ts";

const sessionIdKey = "__aibot_msg_sessionid";

export const getSessionId = () => {
    let sessionId = localStorage.getItem(sessionIdKey)
    if (!sessionId) {
        sessionId = uuid();
        localStorage.setItem(sessionIdKey, sessionId)
    }
    return sessionId;
}


export const resetSessionId = () => {
    localStorage.removeItem(sessionIdKey)
}