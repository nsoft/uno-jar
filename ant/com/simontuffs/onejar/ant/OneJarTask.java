/*
 * Created on Jun 13, 2005
 *
 */
package com.simontuffs.onejar.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
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
    
    FileSet mainFiles;
    
    public static class ManifestFileSet extends FileSet {
        public void setManifest(String manifest) {
            getProject().log("manifest=" + manifest, Project.MSG_VERBOSE);
        }
    }
    
    public void addBoot(FileSet files) {
        getProject().log("addBoot()", Project.MSG_VERBOSE);
        super.addFileset(files);
    }
    
    public void addMain(ManifestFileSet files) {
        getProject().log("addMain()", Project.MSG_VERBOSE);
        mainFiles = files;
        File mainjar = new File(Boot.MAIN_JAR);
        FileSet fileset = new FileSet();
        fileset.setFile(mainjar);
        super.addFileset(fileset);
    }
    
    public void addLib(ZipFileSet files) {
        getProject().log("addLib()", Project.MSG_VERBOSE);
        files.setPrefix("lib/");
        super.addFileset(files);
    }
    
    public void addBinlib(ZipFileSet files) {
        getProject().log("addBinlib()", Project.MSG_VERBOSE);
        files.setPrefix("binlib/");
        super.addFileset(files);
    }

    public void execute() throws BuildException {
        getProject().log("execute()", Project.MSG_VERBOSE);
        super.execute();
    }
    
    
    protected void zipFile(InputStream is, ZipOutputStream zOut, String vPath, long lastModified, File fromArchive,
            int mode) throws IOException {
        if (vPath.equals(Boot.MAIN_JAR)) {
            System.out.println("zipFile(): process " + Boot.MAIN_JAR);
        } else {
            super.zipFile(is, zOut, vPath, lastModified, fromArchive, mode);
        }
    }
}
