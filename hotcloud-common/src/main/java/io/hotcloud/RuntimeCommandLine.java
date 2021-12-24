package io.hotcloud;

import java.io.IOException;

public final class RuntimeCommandLine {

    private RuntimeCommandLine() {
    }

    public static Process exec(String command) throws IOException {
        return exec(new String[]{command});
    }

    public static Process exec(String[] commands) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        return runtime.exec(commands);
    }

    public static <T> T run(String[] commands, RuntimeCommandLineProcessor<T> processor) throws IOException {
        return processor.process(exec(commands));
    }

}
