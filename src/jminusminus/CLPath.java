// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class can be used to locate and load system, extension, and user-defined class files from
 * directories and zip (jar) files.
 */
class CLPath {
    // Stores the individual directories, zip, and jar files from the class path.
    private ArrayList<String> dirs;

    /**
     * Returns a list of conceptual directories defining the class path.
     *
     * @param classPath the directory names defining the class path.
     * @return a list of conceptual directories defining the class path.
     */
    private ArrayList<String> loadClassPath(String classPath) {
        ArrayList<String> container = new ArrayList<String>();

        // Add directories/jars/zips from the classpath.
        StringTokenizer entries = new StringTokenizer(classPath, File.pathSeparator);
        while (entries.hasMoreTokens()) {
            container.add(entries.nextToken());
        }

        // Add system directories.
        if (System.getProperty("sun.boot.class.path") != null) {
            entries = new StringTokenizer(System.getProperty("sun.boot.class.path"),
                    File.pathSeparator);
            while (entries.hasMoreTokens()) {
                container.add(entries.nextToken());
            }
        } else {
            String dir = System.getProperty("java.home") + File.separatorChar + "lib" +
                    File.separatorChar + "rt.jar";
            container.add(dir);
        }
        return container;
    }

    /**
     * Constructs a CLPath object.
     */
    public CLPath() {
        this(null, null);
    }

    /**
     * Constructs a CLPath object given the directory names defining the path and the directory
     * for the Java extension classes.
     *
     * @param path   the directory names defining the class path, separated by path separator.
     * @param extdir the directory for the Java extension classes.
     */
    public CLPath(String path, String extdir) {
        if (path == null) {
            // No path specified, use CLASSPATH.
            path = System.getProperty("java.class.path");
        }
        if (path == null) {
            // Last resort, use current directory.
            path = ".";
        }
        dirs = loadClassPath(path);
        if (extdir == null) {
            // Java extension classes.
            extdir = System.getProperty("java.ext.dirs");
        }
        if (extdir != null) {
            File extDirectory = new File(extdir);
            if (extDirectory.isDirectory()) {
                File[] extFiles = extDirectory.listFiles();
                for (File file : extFiles) {
                    if (file.isFile() &&
                            (file.getName().endsWith(".zip") || file.getName().endsWith(".jar"))) {
                        dirs.add(file.getName());
                    } else {
                        // Wrong suffix; ignore.
                    }
                }
            }
        }
    }

    /**
     * Returns a CLInputStream instance for the class with specified name (fully-qualified;
     * tokens separated by '/') or null if the class was not found.
     *
     * @param name the fully-qualified name of the class (eg, java/util/ArrayList).
     * @return a CLInputStream instance for the class with specified name or null if the class
     * was not found.
     */
    public CLInputStream loadClass(String name) {
        CLInputStream reader = null;
        for (int i = 0; i < dirs.size(); i++) {
            String dir = dirs.get(i);
            File file = new File(dir);
            if (file.isDirectory()) {
                File theClass = new File(dir, name.replace('/', File.separatorChar) + ".class");
                if (theClass.canRead()) {
                    try {
                        reader = new CLInputStream(new BufferedInputStream(new
                                FileInputStream(theClass)));
                    } catch (FileNotFoundException e) {
                        // Ignore
                    }
                }
            } else if (file.isFile()) {
                try {
                    ZipFile zip = new ZipFile(dir);
                    ZipEntry entry = zip.getEntry(name + ".class");
                    if (entry != null) {
                        reader = new CLInputStream(zip.getInputStream(entry));
                    }
                } catch (IOException e) {
                    // Ignore
                }
            } else {
                // Bogus entry; ignore
            }
        }
        return reader;
    }
}

/**
 * This class inherits from java.io.DataInputStream and provides an extra function for reading
 * unsigned int from the input stream, which is required for reading Java class files.
 */
class CLInputStream extends DataInputStream {
    /**
     * Constructs a CLInputStream object from the specified input stream.
     *
     * @param in input stream.
     */
    public CLInputStream(InputStream in) {
        super(in);
    }

    /**
     * Reads four input bytes and returns a {@code long} value in the range 0 through 4294967295.
     * Let a, b, c, d be the four bytes. The value returned is:
     *
     * <pre>
     *   ( b[ 0 ] &amp; 0xFF ) &lt;&lt; 24 ) |
     *   ( ( b[ 1 ] &amp; 0xFF ) &lt;&lt; 16 ) |
     *   ( ( b[ 2 ] &amp; 0xFF ) &lt;&lt; 8 ) |
     *   ( b[ 3 ] &amp; 0xFF )
     * </pre>
     *
     * @return the unsigned 32-bit value.
     * @throws EOFException if this stream reaches the end before reading all the bytes.
     * @throws IOException  if an I/O error occurs.
     */
    public long readUnsignedInt() throws IOException {
        byte[] b = new byte[4];
        long mask = 0xFF, l;
        in.read(b);
        l = ((b[0] & mask) << 24) | ((b[1] & mask) << 16) | ((b[2] & mask) << 8) | (b[3] & mask);
        return l;
    }
}
