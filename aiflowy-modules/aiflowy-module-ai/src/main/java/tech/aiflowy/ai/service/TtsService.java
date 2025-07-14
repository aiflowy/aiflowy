package tech.aiflowy.ai.service;

import okhttp3.WebSocket;

import java.util.function.Consumer;

public interface TtsService {

    WebSocket init(String connectId, String sessionId,Consumer<String> responseHandler,Consumer<String> sessionFinishHandler,Runnable connectionReadyCallback);

    void sendTTSMessage(WebSocket webSocket, String sessionId,String text);

}
