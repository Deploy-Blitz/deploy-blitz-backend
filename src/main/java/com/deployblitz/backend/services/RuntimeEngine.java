package com.deployblitz.backend.services;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;

public interface RuntimeEngine {

    void initializeDaemon(String script, String webHookName);

    void joinToDaemon(Thread thread);

    void executeScript(String script);

    SseEmitter showLogsWebHook(BufferedReader read) throws IOException;
}
