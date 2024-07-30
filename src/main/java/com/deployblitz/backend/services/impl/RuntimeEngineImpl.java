package com.deployblitz.backend.services.impl;

import com.deployblitz.backend.exception.ScriptExtension;
import com.deployblitz.backend.services.RuntimeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RuntimeEngineImpl implements RuntimeEngine {

    private final Map<String, Thread> activeThreads = new ConcurrentHashMap<>();

    @Override
    public void initializeDaemon(String script, String webHookName) {
        var existingThread = activeThreads.get(webHookName);
        if (existingThread != null && existingThread.isAlive()) {
            existingThread.interrupt();
            log.info("Interrupted existing thread: {}", webHookName);
        }

        var newThread = new Thread(() -> executeScript(script, false));
        newThread.setName(webHookName);
        activeThreads.put(webHookName, newThread);
        newThread.start();
    }

    @Override
    public void joinToDaemon(Thread thread) {

    }

    @Override
    public void executeScript(String script, Boolean stopScript) {
        var tempFileLocation = createTempScriptFile(script);
        log.info("Temporary script file location: {}", tempFileLocation);

        var processBuilder = new ProcessBuilder("bash", "-c", tempFileLocation);
        Process process = null;


        try {
            process = processBuilder.start();
            if (stopScript) {
                stopProcess(process, tempFileLocation);
                return ;
            }

            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                showLogsWebHook(reader);
            }

            var code = process.waitFor();
            if (code != 0) {
                throw new ScriptExtension("Script exited with error code: " + code);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error executing script: {}", script, e);
            throw new ScriptExtension("Error executing script", e);
        } finally {
            stopProcess(process, tempFileLocation);
        }
    }

    @Override
    public SseEmitter showLogsWebHook(BufferedReader reader) throws IOException {
        var line = "";
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }
        return null;
    }

    @Override
    public void stopDaemon(String webHookName) {
        var existingThread = activeThreads.get(webHookName);
        if (existingThread != null && existingThread.isAlive()) {
            existingThread.interrupt();
            log.info("Interrupted existing thread: {}", webHookName);
        }
        var newThread = new Thread(() -> executeScript("pwd", true));
        newThread.setName(webHookName);
        activeThreads.put(webHookName, newThread);
        newThread.start();
    }

    private static void stopProcess(Process process, String tempFileLocation) {
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
        }
        try {
            Files.deleteIfExists(new File(tempFileLocation).toPath());
        } catch (IOException e) {
            log.warn("Failed to delete temporary script file: {}", tempFileLocation, e);
        }
    }

    private static String createTempScriptFile(String script) {
        var uuidFile = UUID.randomUUID().toString();
        try {
            var fileTmp = File.createTempFile(uuidFile, ".sh");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileTmp))) {
                writer.write(script);
            }

            if (!fileTmp.setExecutable(true)) {
                throw new ScriptExtension("Failed to set executable permission for temporary script file.");
            }

            return fileTmp.getAbsolutePath();
        } catch (IOException e) {
            throw new ScriptExtension("Error obtaining tmp script " + e.getMessage());
        }

    }


}
