package com.felipefzdz.gradle.shellcheck;

import com.google.gson.Gson;
import org.gradle.api.GradleException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShellcheckInvoker {
    private static final Gson gson = new Gson();
    public static void invoke(ShellcheckTask task) throws IOException, InterruptedException {
        final String processOutput = runShellcheck(task.getShellScripts(), "json");
        System.out.println("processOutput = " + processOutput);
        ShellcheckOutput[] output = gson.fromJson(processOutput, ShellcheckOutput[].class);
        generateReport(output);
        Arrays.stream(output).forEach(entry -> System.out.println(entry.code + " : " + entry.message));
        if (output.length > 0) {
            throw new GradleException("Shellcheck violations were found. " + runShellcheck(task.getShellScripts(), "tty"));
        }
    }

    private static void generateReport(ShellcheckOutput[] output) {
        System.out.println(output.length + " messages in total");
        Arrays.stream(output).collect(Collectors.groupingBy(it -> it.code))
            .forEach((key, value) -> System.out.println(value.size() + " messages of type " + key));

    }

    public static String runShellcheck(File shellScripts, String format) throws IOException, InterruptedException {
        List<String> command = Arrays.asList(
            "docker",
            "run",
            "--rm",
            "-v",
            shellScripts.getAbsolutePath() + ":/mnt",
            "koalaman/shellcheck-alpine:stable",
            "sh",
            "-c",
            "find /mnt -name '*.sh' | xargs shellcheck -f " + format
            );
        System.out.println("command = " + command);
        ProcessBuilder builder = new ProcessBuilder(command)
            .directory(shellScripts)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectErrorStream(true);
        builder.environment().clear();

        builder.redirectErrorStream(true);

        Process process = builder.start();
        StringBuilder processOutput = new StringBuilder();

        try (BufferedReader processOutputReader = new BufferedReader(
            new InputStreamReader(process.getInputStream()));) {
            String readLine;
            while ((readLine = processOutputReader.readLine()) != null) {
                processOutput.append(readLine + System.lineSeparator());
            }
            process.waitFor();
        }
        return processOutput.toString().trim();
    }

    static class ShellcheckOutput {
        private String code;
        private String message;
    }
}