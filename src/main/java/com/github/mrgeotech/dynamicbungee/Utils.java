package com.github.mrgeotech.dynamicbungee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static String[] getChildren(File parent) {
        return parent.list((current, name) -> new File(current, name).isDirectory());
    }

    public static List<File> getChildrenFile(File parent) {
        List<File> directories = new ArrayList<>();
        for (String name : Utils.getChildren(parent)) {
            directories.add(new File(name));
        }
        return directories;
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

}
