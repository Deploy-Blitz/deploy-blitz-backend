package com.deployblitz.backend.services.impl;

import com.deployblitz.backend.exception.ScriptExtension;
import com.deployblitz.backend.services.RuntimeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class RuntimeEngineImpl implements RuntimeEngine {

    private final Map<String, Thread> activeThreads = new ConcurrentHashMap<>();
    private final Map<String, Process> activeProcesses = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void initializeDaemon(String script, String webHookName) {
        stopDaemon(webHookName);

        var newThread = new Thread(() -> executeScript(script, webHookName));
        newThread.setName(webHookName);
        activeThreads.put(webHookName, newThread);
        newThread.start();
    }

    @Override
    public void executeScript(String script, String webHookName) {
        var tempFileLocation = createTempScriptFile(script);
        log.info("Temporary script file location: {}", tempFileLocation);

        var processBuilder = new ProcessBuilder("bash", "-c", tempFileLocation);
        Process process;

        try {
            process = processBuilder.start();
            activeProcesses.put(webHookName, process);
            log.info("Process started: {}", process);

            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            var emitter = showLogsWebHook(reader, webHookName);

            var exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new ScriptExtension("Script exited with error code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            if (Thread.currentThread().isInterrupted()) {
                log.info("Script execution was interrupted");
                return;
            }
            log.error("Error executing script: {}", script, e);
            throw new ScriptExtension("Error executing script", e);
        } finally {
            activeProcesses.remove(webHookName);
        }
    }

    private SseEmitter handleProcessOutput(BufferedReader reader, String webHookName) {
        var emitter = new SseEmitter(0L);
        emitters.put(webHookName, emitter);
        var emitterMap = emitters.get(webHookName);

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
                if (emitterMap != null) {
                    try {
                        emitter.send(SseEmitter.event().data(line));
                    } catch (IOException e) {
                        log.error("Error sending log lines to SseEmitter", e);
                        emitter.completeWithError(e);
                        emitters.remove(webHookName);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error reading script output", e);
        } finally {
            if (emitterMap != null) {
                emitter.complete();
                emitters.remove(webHookName);
            }
        }

        return emitter;
    }

    @Override
    public SseEmitter showLogsWebHook(BufferedReader reader, String webHookName) throws IOException {
        var emitter = new SseEmitter(0L);
        var flagStop = new AtomicBoolean(false);
        emitters.put(webHookName, emitter);
        var thread = new Thread(() -> {
            try {
                var line = "";
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                    try {
                        emitter.send(SseEmitter.event().data(line));
                    } catch (IOException e) {
                        log.warn("SseEmitter connection closed for {}", webHookName);
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("Error sending log lines to SseEmitter", e);
            } finally {
                flagStop.set(true);
                //emitters.remove(webHookName);
            }
        });
        thread.start();
        if (flagStop.get()) {
            thread.interrupt();
        }

        return emitter;
    }

    @Override
    public SseEmitter getEmitter(String webHookName) {
        //getSystemInfo(webHookName);
        return emitters.get(webHookName);
    }

    @Override
    public SseEmitter getSystemInfo(String webHookName) {
        var emitter = new SseEmitter(0L);
        emitters.put(webHookName, emitter);
        new Thread(() -> {
            try {
                var threadMap = activeThreads.get(webHookName);
                var threadMXBean = ManagementFactory.getThreadMXBean();

                for (var threadId : threadMXBean.getAllThreadIds()) {
                    var threadName = threadMXBean.getThreadInfo(threadId).getThreadName();
                    if (threadName.equals(webHookName)) {
                        var cpuTime = threadMXBean.getThreadCpuTime(threadId);
                        var cpuUsage = threadMXBean.getThreadInfo(threadId);
                        emitter.send(SseEmitter.event().data("Thread: " + threadName + ", CPU Time: " + cpuTime + " ns Info: " + cpuUsage));
                    }

                }
            } catch (Exception e) {
                log.error("Error sending log lines to SseEmitter", e);
            } finally {
                emitter.complete();
            }
        }).start();
        return emitter;
    }

    @Override
    public void stopDaemon(String webHookName) {
        var existingThread = activeThreads.get(webHookName);
        if (existingThread != null && existingThread.isAlive()) {
            log.info("Interrupting existing thread: {}", webHookName);
            existingThread.interrupt();

            var process = activeProcesses.get(webHookName);
            if (process != null) {
                process.destroyForcibly();
                log.info("Terminated process associated with thread: {}", webHookName);
            }

            try {
                existingThread.join();
                if (!existingThread.isAlive()) {
                    log.info("Existing thread has been joined: {}", webHookName);
                }
            } catch (InterruptedException e) {
                log.warn("Interrupted while waiting for existing thread to finish: {}", webHookName, e);
                Thread.currentThread().interrupt();
            }
        }
        var emitter = emitters.remove(webHookName);
        if (emitter != null) {
            emitter.complete();
        }
        activeThreads.remove(webHookName);
    }

    private static String createTempScriptFile(String script) {
        final var uuidFile = UUID.randomUUID().toString();
        try {
            var tempFile = File.createTempFile(uuidFile, ".sh");
            try (var writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(script);
            }

            if (!tempFile.setExecutable(true)) {
                throw new ScriptExtension("Failed to set executable permission for temporary script file.");
            }

            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            throw new ScriptExtension("Error obtaining tmp script " + e.getMessage());
        }
    }
}
