package com.github.mrgeotech.dynamicbungee;

import net.md_5.bungee.config.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Utils {

    /**
     * Gets all the names of directories in a parent directory
     *
     * @param parent The parent directory
     * @return A list of names of each child directory
     */
    public static String[] getChildren(File parent) {
        return parent.list((current, name) -> new File(current, name).isDirectory());
    }

    /**
     * Gets all the directories in a parent directory
     *
     * @param parent The parent directory
     * @return A list of children directories
     */
    public static File[] getChildrenFile(File parent) {
        return parent.listFiles((current, name) -> new File(current, name).isDirectory());
    }

    /**
     * Deletes all the files in a parent directory
     *
     * @param parent The parent directory
     */
    public static void deleteChildren(File parent) {
        for (File child : parent.listFiles()) {
            if (child.isDirectory()) {
                Utils.deleteChildren(child);
            }
            child.delete();
        }
    }

    /**
     * Copies files from one directory to anther
     *
     * @param sourceDirectoryLocation The directory getting copied
     * @param destinationDirectoryLocation The location directory
     */
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

    /**
     * Changes the port in the server.properties files
     * so that the server starts on the correct port.
     *
     * @param parent The location of the directory that contains the server.properties file
     * @param port The port to set the servers port to
     */
    public static void updatePort(File parent, int port) throws IOException {
        FileInputStream in = new FileInputStream(new File(parent, "server.properties"));
        Properties props = new Properties();
        props.load(in);
        in.close();

        FileOutputStream out = new FileOutputStream(new File(parent, "server.properties"));
        props.setProperty("server-port", String.valueOf(port));
        props.store(out, null);
        out.close();
    }

    /**
     * Checks if a port is open
     *
     * @param port The port to check
     * @return Whether the port is open or not
     */
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

    public static String getDownloadUrl(String type, String version) {
        if (type.equalsIgnoreCase("paper")) {
            try {
                JSONObject json = getJsonFromURL(new URL("https://papermc.io/api/v2/projects/paper/versions/" + version + "/"));
                JSONArray builds = json.getJSONArray("builds");
                int build = builds.getInt(builds.length() - 1);
                return "https://papermc.io/api/v2/projects/paper/versions/" + version + "/builds/" + build + "/downloads/paper-" + version + "-" + build + ".jar";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONObject getJsonFromURL(URL url) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String document = "";
        String temp;
        while ((temp = reader.readLine()) != null) {
            document += temp;
        }
        reader.close();
        return new JSONObject(document);
    }

}
