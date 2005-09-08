/*
 * Created on Jun 13, 2005
 *
 */
package com.simontuffs.onejar.ant;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.zip.ZipOutputStream;

import com.simontuffs.onejar.Boot;

/**
 * @author simon
 * The One-Jar Ant task.  Extends &lt;jar>
 *
 */
public class OneJarTask extends Jar {
    
    public static final int BUFFER_SIZE = 8192;
    public static final String META_INF_MANIFEST = "META-INF/MANIFEST.MF";
    public static final String MAIN_MAIN_JAR = "main/main.jar";
    
    protected Main main;
    protected ZipFile onejar;
    protected File mainManifest;
    
    public static class Main extends Task {
        protected List filesets = new ArrayList();
        protected File manifest;
        public void setManifest(File manifest) {
            this.manifest = manifest;
        }
        public void addFileSet(FileSet fileset) {
            getProject().log("Main.addFileSet() ", Project.MSG_VERBOSE);
            filesets.add(fileset);
        }
    }
    
    public static class Lib extends Task {
        protected List filesets = new ArrayList();
        public void addFileSet(ZipFileSet fileset) {
            getProject().log("Lib.addFileSet() ", Project.MSG_VERBOSE);
            filesets.add(fileset);
        }
    }

    public static class BinLib extends Task {
        protected List filesets = new ArrayList();
        public void addFileSet(ZipFileSet fileset) {
            getProject().log("BinLib.addFileSet() ", Project.MSG_VERBOSE);
            filesets.add(fileset);
        }
    }

    public void setBootManifest(File manifest) {
        getProject().log("setBootManifest(" + manifest + ")", Project.MSG_VERBOSE);
        super.setManifest(manifest);
    }

    /**
     * Use <main manifest="file"> instead of this method.  This is here for
     * compatibility with the one-jar-macro.
     * @param manifest
     */
    public void setMainManifest(File manifest) {
        mainManifest = manifest;
        getProject().log("setMainManifest(" + manifest + ")", Project.MSG_VERBOSE);
    }

    public void setOneJarBoot(ZipFile jar) {
        getProject().log("setOneJarBoot(" + jar + ")", Project.MSG_VERBOSE);
        this.onejar = jar;
    }

    public void addBoot(ZipFileSet files) {
        getProject().log("addBoot()", Project.MSG_VERBOSE);
        super.addFileset(files);
    }
    
    public void addMain(Main main) {
        this.main =  main;
    }
    
    public void addConfiguredLib(Lib lib) {
        getProject().log("addLib()", Project.MSG_VERBOSE);
        Iterator iter = lib.filesets.iterator();
        while (iter.hasNext()) {
            ZipFileSet fileset = (ZipFileSet)iter.next();
            fileset.setPrefix("lib/");
            super.addFileset(fileset);
        }
    }
    
    public void addConfiguredBinLib(BinLib lib) {
        getProject().log("addBinLib()", Project.MSG_VERBOSE);
        Iterator iter = lib.filesets.iterator();
        while (iter.hasNext()) {
            ZipFileSet fileset = (ZipFileSet)iter.next();
            fileset.setPrefix("binlib/");
            super.addFileset(fileset);
        }
    }
    
    
    protected class PipedThread extends Thread {
        protected boolean done = false;
        protected PipedInputStream pin = new PipedInputStream();
        protected PipedOutputStream pout = new PipedOutputStream();
        
        protected Set entries = new HashSet();
        public PipedThread() {
            try {
                pin.connect(pout);
            } catch (IOException iox) {
                throw new BuildException(iox);
            }
        }
        
    }

    /**
     * Convert a fileset into an input stream which appears as though it was
     * read from a Zip file.
     * @author simon
     *
     */
    protected class FileSetPump extends PipedThread {
        protected String target;
        
        public FileSetPump(String target) {
            this.target = target;
        }
        
        public void run() {
            // Create the main/main.jar entry in the output file, and suck in
            // all <filesets> from <main>.  
            // TODO: Ignore duplicates (first takes precedence).
            Iterator iter = main.filesets.iterator();
            java.util.zip.ZipOutputStream zout = new java.util.zip.ZipOutputStream(pout);
            
            
            try {
                // Write the manifest file.
                ZipEntry m = new ZipEntry(META_INF_MANIFEST);
                zout.putNextEntry(m);
                copy("Created-By: One-Jar 0.96 Ant taskdef\n", zout);
                if (main.manifest != null) {
                    copy(new FileInputStream(main.manifest), zout);
                } else if (mainManifest != null) {
                    copy(new FileInputStream(mainManifest), zout);
                }
                zout.closeEntry();
                // Now the rest of the main.jar entries
                while (iter.hasNext()) {
                    FileSet fileset = (FileSet)iter.next();
                    FileScanner scanner = fileset.getDirectoryScanner(getProject());
                    String[] files = scanner.getIncludedFiles();
                    File basedir = scanner.getBasedir();
                    for (int i=0; i<files.length; i++) {
                        String file = files[i].replace('\\', '/');
                        if (entries.contains(file)) {
                            getProject().log("Duplicate entry " + target + " (ignored): " + file, Project.MSG_WARN);
                            continue;
                        }
                        entries.add(file);
                        ZipEntry ze = new ZipEntry(file);
                        zout.putNextEntry(ze);
                        getProject().log("processing " + file, Project.MSG_DEBUG);
                        FileInputStream fis = new FileInputStream(new File(basedir, file));
                        copy(fis, zout);
                        zout.closeEntry();
                    }
                }
                zout.close();
                synchronized(this) {
                    done = true;
                    notify();
                }
            } catch (IOException iox) {
                throw new BuildException(iox);
            }
        }
    }
    
    protected void includeZip(ZipFile zip, ZipOutputStream zOut) {
        try {
            Enumeration entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                getProject().log("ZipPump: " + entry.getName(), Project.MSG_DEBUG);
                super.zipFile(zip.getInputStream(entry), zOut, entry.getName(), System.currentTimeMillis(), null, ZipFileSet.DEFAULT_FILE_MODE);
            }
        } catch (IOException iox) {
            throw new BuildException(iox);
        }
    }

    protected byte buf[] = new byte[BUFFER_SIZE];
    protected void copy(InputStream is, OutputStream os) throws IOException {
        int len = -1;
        while ((len = is.read(buf)) >= 0) {
            os.write(buf, 0, len);
        }
    }
    
    protected void copy(String s, OutputStream os) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
        copy(bais, os);
    }
    
    protected void initZipOutputStream(ZipOutputStream zOut) throws IOException, BuildException {
        super.initZipOutputStream(zOut);
        
        
        ByteArrayInputStream devnull = new ByteArrayInputStream(new byte[0]);
        super.zipFile(devnull, zOut, "main", System.currentTimeMillis(), null, ZipFileSet.DEFAULT_DIR_MODE);

        // main/main.jar
        FileSetPump pump = new FileSetPump(MAIN_MAIN_JAR);
        pump.start();
        super.zipFile(pump.pin, zOut, MAIN_MAIN_JAR, System.currentTimeMillis(), null, ZipFileSet.DEFAULT_FILE_MODE);

        // One-jar bootstrap files
        includeZip(onejar, zOut);
        
    }
    
    public void execute() throws BuildException {
        getProject().log("execute()", Project.MSG_VERBOSE);
        // First create a main.jar from the main filesets.
        
        // Then, add all files to the final jar.
        super.execute();
    }
    
    
    protected void zipFile(InputStream is, ZipOutputStream zOut, String vPath, long lastModified, File fromArchive,
            int mode) throws IOException {
        if (vPath.equals(Boot.MAIN_JAR)) {
            getProject().log("zipFile(): process " + Boot.MAIN_JAR, Project.MSG_DEBUG);
        } else {
            super.zipFile(is, zOut, vPath, lastModified, fromArchive, mode);
        }
    }
}
