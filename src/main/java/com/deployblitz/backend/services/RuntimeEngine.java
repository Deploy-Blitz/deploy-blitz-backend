package com.deployblitz.backend.services;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;

public interface RuntimeEngine {

    void initializeDaemon(String script, String webHookName);

    void executeScript(String script, String webHookName);

    SseEmitter showLogsWebHook(BufferedReader read, String webHookName) throws IOException;

    void stopDaemon(String webHookName);

    SseEmitter getEmitter(String webHookName);
}
