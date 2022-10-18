package com.yuri.empire.utils;

import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class CmdUtils {

    public synchronized static boolean createFolder(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                String cmd = "mkdir -p " + path;
                Process exec = Runtime.getRuntime().exec(cmd);
                exec.waitFor();
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public synchronized static boolean deleteFolder(String path) {
        try {
            File file = new File(path);
            if (file.exists() && !path.equals("*")) {
                String cmd = "rm -rf " + path;
                Process exec = Runtime.getRuntime().exec(cmd);
                exec.waitFor();
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public synchronized static boolean createFolder(String path, boolean isForce) {
        try {
            File file = new File(path);
            if (isForce && file.exists() && !path.equals("*")) {
                String cmd = "rm -rf " + path;
                Process exec = Runtime.getRuntime().exec(cmd);
                exec.waitFor();
            }
            return createFolder(path);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public synchronized static boolean copyFolder(String fromPath, String toPath, boolean isForce) {
        try {
            createFolder(toPath, isForce);
            String cmd = "cp -r " + fromPath + "/* " + toPath;
            Process exec = Runtime.getRuntime().exec(cmd);
            exec.waitFor();
            InputStreamReader isReader = new InputStreamReader(exec.getErrorStream());
            BufferedReader bReader = new BufferedReader(isReader);
            String strLine;
            boolean result = true;
            while ((strLine = bReader.readLine()) != null) {
                log.info(strLine);
                result = false;
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public synchronized static boolean removeFile(String path) {
        try {
            log.info("File remove: {}", path);
            File file = new File(path);
            if (file.exists() && !path.equals("*")) {
                String cmd = "rm -rf " + path;
                Process exec = Runtime.getRuntime().exec(cmd);
                exec.waitFor();
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean execute(List<String> commandList) {
        try {
            for (String cmd : commandList) {
                Process exec = Runtime.getRuntime().exec(cmd);
                exec.waitFor();
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static long execute(String cmd) {
        try {
            Process exec = Runtime.getRuntime().exec(cmd);
            exec.waitFor();
            return exec.pid();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return -1;
        }
    }

    public static long executeForget(String cmd) {
        try {
            log.error("Execute: {}", cmd);
            Process exec = Runtime.getRuntime().exec(cmd);
            return exec.pid();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return -1;
        }
    }

    public static long executeAndGetLog(String cmd) {
        try {
            log.error("Execute: {}", cmd);
            Process exec = Runtime.getRuntime().exec(cmd);
            new Thread(() -> {
                InputStreamReader isReader = new InputStreamReader(exec.getInputStream());
                BufferedReader bReader = new BufferedReader(isReader);
                String strLine;
                try {
                    while ((strLine = bReader.readLine()) != null) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            return exec.pid();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return -1;
        }
    }

    public static boolean isProcessIdRunning(long pid) {
        String command = "ps -p " + pid;
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);
            pr.waitFor();

            InputStreamReader isReader = new InputStreamReader(pr.getInputStream());
            BufferedReader bReader = new BufferedReader(isReader);
            String strLine;
            while ((strLine = bReader.readLine()) != null) {
                if (strLine.trim().startsWith(pid + " ")) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            log.warn("Got exception using system command [{}].", command, ex);
            return true;
        }
    }

    public static long killProcess(long pid) {
        log.error("kill pid {}", pid);
        return execute("kill " + pid);
    }

    public static Map<String, Double> getResourceUsage() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        Runtime instance = Runtime.getRuntime();
        long maxMemory = instance.maxMemory();
        long totalMemory = instance.totalMemory();
        long freeMemory = instance.freeMemory();

        double cpuLoad = operatingSystemMXBean.getSystemCpuLoad();
        double memLoad = ((totalMemory - freeMemory) * 1.0) / maxMemory;
        Map<String, Double> stateMap = new HashMap<>();
        stateMap.put("cpu", new BigDecimal(cpuLoad).setScale(5, RoundingMode.FLOOR).doubleValue());
        stateMap.put("mem", new BigDecimal(memLoad).setScale(5, RoundingMode.FLOOR).doubleValue());
        return stateMap;
    }
}
