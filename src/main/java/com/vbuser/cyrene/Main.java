package com.vbuser.cyrene;

import com.vbuser.cyrene.editor.WelcomeServer;
import com.vbuser.cyrene.editor.ProjectEditorServer;
import com.vbuser.cyrene.env.JavaVersionChecker;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("正在关闭文本编辑器...");
            if (WelcomeServer.getServer() != null) {
                WelcomeServer.stopServer();
            }
            if (ProjectEditorServer.getServer() != null) {
                ProjectEditorServer.stopServer();
            }
        }));

        System.out.println("启动文本编辑器...");
        System.out.println("当前Java版本: " + System.getProperty("java.version"));
        System.out.println("当前工作目录: " + System.getProperty("user.dir"));

        try {
            if (JavaVersionChecker.isJava8()) {
                System.out.println("运行在 Java 8 环境中");
                launch();
            } else {
                System.out.println("当前运行在 Java " + System.getProperty("java.version") + " 环境中，尝试重启到 Java 8");
                List<String> java8Paths = JavaVersionChecker.findJava8Paths();

                if (!java8Paths.isEmpty()) {
                    System.out.println("找到以下 Java 8 安装路径:");
                    for (String path : java8Paths) {
                        System.out.println("  - " + path);
                    }
                    restartWithJava8(java8Paths.get(0), args);
                } else {
                    System.out.println("警告: 未找到 Java 8 安装，将使用当前 Java 版本运行");
                    System.out.println("某些功能可能无法正常工作");
                    launch();
                }
            }
        } catch (Exception e) {
            System.err.println("启动失败: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void restartWithJava8(String java8Path, String[] args) {
        try {
            System.out.println("正在重启到 Java 8 环境: " + java8Path);

            String javaExec = new File(java8Path, "bin/java").getAbsolutePath();
            if (!new File(javaExec).exists()) {
                javaExec = new File(java8Path, "bin/java.exe").getAbsolutePath();
                if (!new File(javaExec).exists()) {
                    throw new RuntimeException("在路径 " + java8Path + " 中未找到 java 可执行文件");
                }
            }

            String classpath = System.getProperty("java.class.path");
            String mainClass = getMainClassName();

            List<String> command = new ArrayList<>();
            command.add(javaExec);

            List<String> jvmArgs = getJVMArguments();
            command.addAll(jvmArgs);

            command.add("-cp");
            command.add(classpath);
            command.add(mainClass);

            Collections.addAll(command, args);

            System.out.println("重启命令: " + String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO();

            if (WelcomeServer.getServer() != null) {
                WelcomeServer.stopServer();
            }
            if (ProjectEditorServer.getServer() != null) {
                ProjectEditorServer.stopServer();
            }

            pb.start();
            System.exit(0);

        } catch (Exception e) {
            System.err.println("重启失败: " + e.getMessage());
            System.out.println("使用当前 Java 版本继续运行");
            launch();
        }
    }

    private static String getMainClassName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            if ("main".equals(element.getMethodName())) {
                return element.getClassName();
            }
        }
        return Main.class.getName();
    }

    private static List<String> getJVMArguments() {
        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        List<String> filteredArgs = new ArrayList<>();

        for (String arg : jvmArgs) {
            if (!arg.contains("-version") &&
                    !arg.startsWith("-Djava.version") &&
                    !arg.startsWith("-Djava.home") &&
                    !arg.startsWith("-agentlib") &&
                    !arg.contains("TextEditorMain")) {
                filteredArgs.add(arg);
            }
        }

        if (filteredArgs.stream().noneMatch(arg -> arg.startsWith("-Xmx"))) {
            filteredArgs.add("-Xmx512m");
        }

        if (filteredArgs.stream().noneMatch(arg -> arg.startsWith("-Xms"))) {
            filteredArgs.add("-Xms128m");
        }

        return filteredArgs;
    }

    public static void launch() {
        try {
            System.out.println("启动文本编辑器服务器...");
            WelcomeServer.startServer();
        } catch (Exception e) {
            System.err.println("启动过程出错: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void openBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Runtime runtime = Runtime.getRuntime();

            if (os.contains("win")) {
                runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                runtime.exec(new String[]{"open", url});
            } else if (os.contains("nix") || os.contains("nux")) {
                runtime.exec(new String[]{"xdg-open", url});
            } else {
                System.out.println("无法自动打开浏览器，请手动访问: " + url);
            }
        } catch (IOException e) {
            System.err.println("自动打开浏览器失败: " + e.getMessage());
            System.out.println("请手动访问: " + url);
        }
    }
}