package com.github.mrgeotech.dynamicbungee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Utils {

    public static String[] getChildren(File parent) {
        return parent.list((current, name) -> new File(current, name).isDirectory());
    }

    public static File[] getChildrenFile(File parent) {
        return parent.listFiles((current, name) -> new File(current, name).isDirectory());
    }

    public static void deleteChildren(File parent) {
        for (File child : parent.listFiles()) {
            if (child.isDirectory()) {
                Utils.deleteChildren(child);
            }
            child.delete();
        }
    }

    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) {
        try {
            new File(destinationDirectoryLocation).mkdirs();
            Files.walk(Paths.get(sourceDirectoryLocation))
                    .forEach(source -> {
                        Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                                .substring(sourceDirectoryLocation.length()));
                        try {
                            Files.copy(source, destination);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (Exception ignore) {}
    }

    public static int getPortFromDirectory(File file) {
        try {
            Scanner scanner = new Scanner(new File(file.getAbsolutePath() + "/server.properties"));
            String line;
            while (scanner.hasNextLine()) {
                if ((line = scanner.nextLine()).contains("server-port")) {
                    return Integer.parseInt(line.replaceAll("server-port=", ""));
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isPortOpen(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

}
