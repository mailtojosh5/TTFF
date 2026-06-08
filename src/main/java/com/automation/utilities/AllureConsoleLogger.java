package com.automation.utilities;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.qameta.allure.Allure;

public class AllureConsoleLogger {

    private static final PrintStream originalOut = System.out;
    private static final ThreadLocal<List<String>> LOGS = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<StringBuilder> LINE_BUFFER = ThreadLocal.withInitial(StringBuilder::new);
    private static volatile boolean initialized = false;

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;

        PrintStream capturingStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                originalOut.write(b);
                if (b == '\n') {
                    flushLineBuffer();
                } else if (b != '\r') {
                    LINE_BUFFER.get().append((char) b);
                }
            }

            @Override
            public void write(byte[] b, int off, int len) {
                originalOut.write(b, off, len);
                String chunk = new String(b, off, len, StandardCharsets.UTF_8);
                for (char c : chunk.toCharArray()) {
                    if (c == '\n') {
                        flushLineBuffer();
                    } else if (c != '\r') {
                        LINE_BUFFER.get().append(c);
                    }
                }
            }

            private void flushLineBuffer() {
                StringBuilder buffer = LINE_BUFFER.get();
                if (buffer.length() > 0) {
                    LOGS.get().add(buffer.toString());
                    buffer.setLength(0);
                }
            }
        }, true, StandardCharsets.UTF_8);

        System.setOut(capturingStream);
    }

    public static void clear() {
        LOGS.remove();
        LINE_BUFFER.remove();
    }

    public static List<String> getLogs() {
        flushCurrentLine();
        return Collections.unmodifiableList(new ArrayList<>(LOGS.get()));
    }

    private static void flushCurrentLine() {
        StringBuilder buffer = LINE_BUFFER.get();
        if (buffer.length() > 0) {
            LOGS.get().add(buffer.toString());
            buffer.setLength(0);
        }
    }

    public static void attachLogs(String title) {
        List<String> logs = getLogs();
        if (logs.isEmpty()) {
            return;
        }
        Allure.step(title, () -> logs.forEach(AllureConsoleLogger::attachLogLine));
    }

    private static void attachLogLine(String logLine) {
        try {
            Allure.step(logLine);
        } catch (Exception ignored) {
            // ignore when Allure not available
        }
    }
}
