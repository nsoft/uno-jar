/*
 * Created on Jun 13, 2005
 *
 */
package com.needhamsoftware.unojar.ant;

import com.needhamsoftware.unojar.Boot;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.Manifest.Attribute;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * @author simon
 * The Uno-Jar Ant task.  Extends &lt;jar&gt;
 */
@SuppressWarnings("unused")
public class UnoJarTask extends Jar {
  public static final int BUFFER_SIZE = 8192;
  public static final String META_INF_MANIFEST = "META-INF/MANIFEST.MF";
  public static final String MAIN_MAIN_JAR = "main/main.jar";
  public static final String CLASS = ".class";
  public static final String MAIN_CLASS = Attributes.Name.MAIN_CLASS.toString();
  public static final String MULTI_RELEASE = "Multi-Release";

  protected Main main;
  protected MainJars mainJars;
  protected ZipFile unoJar;
  protected File mainManifest;
  protected String oneJarMainClass;
  protected boolean manifestSet;

  @SuppressWarnings("unused")
  public static class Main extends Task {
    protected List<FileSet> filesets = new ArrayList<>();
    protected File manifest;
    protected File jar;

    public void setManifest(File manifest) {
      this.manifest = manifest;
    }

    public void addFileSet(FileSet fileset) {
      if (jar != null)
        throw new BuildException("'jar' attribute is mutually exclusive to use of <fileset>");
      log("Main.addFileSet() ", Project.MSG_VERBOSE);
      filesets.add(fileset);
    }

    public void setJar(File main) {
      jar = main;
    }
  }

  @SuppressWarnings("unused")
  public static class MainJars extends Task {
    List<ZipFileSet> filesets = new ArrayList<>();
    protected String app;

    public void addFileSet(ZipFileSet fileset) {
      log("MainJar.addFileSet() ", Project.MSG_VERBOSE);
      filesets.add(fileset);
    }
  }

  @SuppressWarnings("unused")
  public static class Lib extends Task {
    protected List<ZipFileSet> filesets = new ArrayList<>();

    public void addFileSet(ZipFileSet fileset) {
      log("Lib.addFileSet() ", Project.MSG_VERBOSE);
      filesets.add(fileset);
    }

    public void addConfiguredClasspath(final Path classpath) {
      log("adding classpath: " + classpath, Project.MSG_VERBOSE);
      for (Resource res : classpath) {
        if (res instanceof FileResource) {
          final FileResource fileResource = (FileResource) res;
          log("res.name: " + fileResource.getName()
                          + " res.exists: " + fileResource.isExists()
                          + " res.class: " + fileResource.getClass().getName()
                          + " res.file: " + fileResource.getFile()
                  , Project.MSG_DEBUG);
          final File dir = fileResource.getFile().getParentFile();
          final String name = fileResource.getFile().getName();
          final ZipFileSet fileset = new ZipFileSet();
          fileset.setProject(getProject());
          fileset.setDir(dir);
          fileset.createInclude().setName(name);
          filesets.add(fileset);
        } else {
          throw new BuildException("Not a file resource: " + res);
        }
      }
    }
  }



  @SuppressWarnings("unused")
  public static class BinLib extends Task {
    protected List<ZipFileSet> filesets = new ArrayList<>();

    public void addFileSet(ZipFileSet fileset) {
      log("BinLib.addFileSet() ", Project.MSG_VERBOSE);
      filesets.add(fileset);
    }
  }

  /**
   * Default constructor.
   */
  @SuppressWarnings("unused")
  public UnoJarTask() {
    super();
  }

  /**
   * Constructor for use outside Ant: creates a wrapping project and gives it a name.
   *
   * @param project the project we are running under
   */
  @SuppressWarnings("unused")
  public UnoJarTask(String project) {
    super();
    setTaskName("uno-jar");
    setProject(new Project());
    getProject().setName(project);
  }

  public void setBootManifest(File manifest) {
    log("setBootManifest(" + manifest + ")", Project.MSG_VERBOSE);
    super.setManifest(manifest);
  }

  /**
   * Use &lt;main manifest="file"&gt; instead of this method.
   *
   * @param manifest the manifest to use
   */
  public void setMainManifest(File manifest) {
    mainManifest = manifest;
    log("setMainManifest(" + manifest + ")", Project.MSG_VERBOSE);
  }

  public void setOneJarMainClass(String main) {
    oneJarMainClass = main;
  }

  public void setOneJarBoot(ZipFile jar) {
    log("setOneJarBoot(" + jar + ")", Project.MSG_VERBOSE);
    this.unoJar = jar;
  }

  public void addBoot(ZipFileSet files) {
    log("addBoot()", Project.MSG_VERBOSE);
    super.addFileset(files);
  }

  public void addMain(Main main) {
    log("addMain()", Project.MSG_VERBOSE);
    this.main = main;
  }

  public void addConfiguredLib(Lib lib) {
    log("addLib()", Project.MSG_VERBOSE);
    for (ZipFileSet fileset : lib.filesets) {
      fileset.setPrefix("lib/");
      super.addFileset(fileset);
    }
  }

  public void addConfiguredManifest(Manifest newManifest) throws ManifestException {
    super.addConfiguredManifest(newManifest);
    manifestSet = true;
  }

  public void addConfiguredMainJars(MainJars jars) {
    this.mainJars = jars;
    log("addMainJar()", Project.MSG_VERBOSE);
    for (ZipFileSet fileset : jars.filesets) {
      fileset.setPrefix("main/");
      super.addFileset(fileset);
    }
  }



  public void addConfiguredBinLib(BinLib lib) {
    log("addBinLib()", Project.MSG_VERBOSE);
    for (ZipFileSet fileset : lib.filesets) {
      fileset.setPrefix("binlib/");
      super.addFileset(fileset);
    }
  }


  protected static class PipedThread extends Thread {
    protected boolean done = false;
    protected PipedInputStream pin = new PipedInputStream();
    protected PipedOutputStream pout = new PipedOutputStream();

    protected Set<String> entries = new HashSet<>();

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
   *
   * @author simon
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
      if (main != null) {
        Iterator<FileSet> iter = main.filesets.iterator();
        java.util.zip.ZipOutputStream zOut = new java.util.zip.ZipOutputStream(pout);


        try {
          // Write the manifest file.
          ZipEntry m = new ZipEntry(META_INF_MANIFEST);
          zOut.putNextEntry(m);
          if (main.manifest != null) {
            copy(new FileInputStream(main.manifest), zOut, true);
          } else if (mainManifest != null) {
            copy(new FileInputStream(mainManifest), zOut, true);
          }
          zOut.closeEntry();
          // Now the rest of the main.jar entries
          while (iter.hasNext()) {
            FileSet fileset = iter.next();
            FileScanner scanner = fileset.getDirectoryScanner(getProject());
            String[] files = scanner.getIncludedFiles();
            File basedir = scanner.getBasedir();
            for (String s : files) {
              String file = s.replace('\\', '/');
              if (entries.contains(file)) {
                log("Duplicate entry " + target + " (ignored): " + file, Project.MSG_WARN);
                continue;
              }
              entries.add(file);
              // Add any directory entries that have not already been added.
              String p = new File(file).getParent();
              if (p != null) {
                String dirs = p.replace('\\', '/');
                if (!entries.contains(dirs)) {
                  String[] tokens = dirs.split("/");
                  String dir = "";
                  for (String tok : tokens) {
                    dir += tok + "/";
                    if (!entries.contains(dir)) {
                      ZipEntry ze = new ZipEntry(dir);
                      zOut.putNextEntry(ze);
                      // Suppress FindBugs warning.
                      zOut.flush();
                      zOut.closeEntry();
                      entries.add(dir);
                    }
                  }
                  entries.add(dir);
                }
              }

              ZipEntry ze = new ZipEntry(file);
              zOut.putNextEntry(ze);
              log("processing " + file, Project.MSG_DEBUG);
              FileInputStream fis = new FileInputStream(new File(basedir, file));
              copy(fis, zOut, true);
              zOut.closeEntry();
            }
          }
          zOut.close();
          synchronized (this) {
            done = true;
            notify();
          }
        } catch (IOException iox) {
          throw new BuildException(iox);
        }
      }
    }
  }

  protected void includeZip(ZipFile zip, ZipOutputStream zOut) {
    try {
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        if (entry.getName().endsWith(CLASS) || entry.getName().equals(".version")) {
          log("ZipPump: " + entry.getName(), Project.MSG_DEBUG);
          super.zipFile(zip.getInputStream(entry), zOut, entry.getName(), System.currentTimeMillis(), null, ZipFileSet.DEFAULT_FILE_MODE);
        }
      }
    } catch (IOException iox) {
      throw new BuildException(iox);
    }
  }

  protected byte[] buf = new byte[BUFFER_SIZE];

  protected void copy(InputStream is, OutputStream os, boolean closeIn) throws IOException {
    int len;
    while ((len = is.read(buf)) >= 0) {
      os.write(buf, 0, len);
    }
    if (closeIn) is.close();
  }

  protected void copy(String s, OutputStream os) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
    copy(bais, os, true);
  }

  public void setManifest(File manifestFile) {
    super.setManifest(manifestFile);
    manifestSet = true;
  }

  protected void checkMain() {
    if (mainJars == null && main == null)
      throw new BuildException("No <main> or <mainjars> element found in the <uno-jar> task!");
  }

  protected void checkManifest() {
    if (!manifestSet)
      log("No 'manifest' attribute was specified for the <uno-jar> task, a default manifest will be generated.", Project.MSG_WARN);
  }

  protected void addOneJarBoot(ZipOutputStream zOut) throws IOException {
    // BUG-2674591: filesetmanifest attribute leads to null zOut: ignore.
    if (zOut == null)
      return;
    // Uno-Jar bootstrap files
    if (unoJar != null) {
      // this path seems to assume the user is adding their own manifest at a later time
      // the manifest from the specified jar is not copied.
      includeZip(unoJar, zOut);
    } else {
      // Pick up default uno-jar boot files as a resource relative to
      // this class.
      String ONE_JAR_BOOT = "core.jar";
      InputStream is = UnoJarTask.class.getResourceAsStream(ONE_JAR_BOOT);
      if (is == null)
        throw new IOException("Unable to load default " + ONE_JAR_BOOT + ": consider using the <uno-jar onejarboot=\"...\"> option.");
      // Pull the manifest out and use it.
      JarInputStream jis = new JarInputStream(is);
      Manifest manifest = new Manifest();
      try {
        // Specify our Created-By and Main-Class attributes as overrides.
        manifest.addConfiguredAttribute(new Attribute("Archive-Type", "uno-jar"));
        // Slightly ugly to hard code the class name, but it will be caught by unit tests if it changes.
        // Avoids having an oddball Main-Class declaration in the library jar file, just so we can
        // pull it out of the manifest and use it here. Specific tests that check this are the tests
        // run by AntTests which will all fail if this class name is incorrect.
        manifest.addConfiguredAttribute(new Attribute(MAIN_CLASS,"com.needhamsoftware.unojar.Boot"));
        manifest.addConfiguredAttribute(new Attribute(MULTI_RELEASE,"true"));
        if (oneJarMainClass != null) {
          manifest.addConfiguredAttribute(new Attribute(Boot.ONE_JAR_MAIN_CLASS, oneJarMainClass));
        }
        super.addConfiguredManifest(manifest);
      } catch (ManifestException mx) {
        throw new BuildException(mx);
      }
      super.initZipOutputStream(zOut);
      ZipEntry entry = jis.getNextEntry();
      while (entry != null) {
        if (entry.getName().endsWith(CLASS) ||
            entry.getName().equals(".version") ||
            entry.getName().endsWith("LICENSE.txt") ||
            entry.getName().endsWith("NOTICE.txt")
        ) {
          log("entry=" + entry.getName(), Project.MSG_DEBUG);
          zOut.putNextEntry(new org.apache.tools.zip.ZipEntry(entry));
          copy(jis, zOut, false);
        }
        entry = jis.getNextJarEntry();
      }

    }
  }

  protected void addMain(ZipOutputStream zOut) throws IOException {
    // Already constructed?
    if (main == null || main.jar != null)
      return;
    // Assemble main/main.jar
    FileSetPump pump = new FileSetPump(MAIN_MAIN_JAR);
    pump.start();
    super.zipFile(pump.pin, zOut, MAIN_MAIN_JAR, System.currentTimeMillis(), null, ZipFileSet.DEFAULT_FILE_MODE);

  }

  protected void initZipOutputStream(ZipOutputStream zOut) throws IOException {

    // Sanity Checks.
    checkMain();
    checkManifest();

    // Add com.needhamsoftware.unojar classes
    addOneJarBoot(zOut);

    // Add main/main.jar
    addMain(zOut);

  }

  public void execute() throws BuildException {
    log("execute()", Project.MSG_VERBOSE);
    // First include a main.jar if specified.
    if (main != null && main.jar != null) {
      ZipFileSet fs = new ZipFileSet();
      fs.setProject(getProject());
      fs.setFile(main.jar);
      fs.setPrefix("main/");
      System.out.println("main.jar fs=" + fs);
      super.addFileset(fs);
    }

    // Then, add all files to the final jar.
    super.execute();
  }


  protected void zipFile(InputStream is, ZipOutputStream zOut, String vPath, long lastModified, File fromArchive,
                         int mode) throws IOException {
    if (vPath.equals(Boot.MAIN_JAR) && (main == null || main.jar == null)) {
      log("zipFile(): unable to build " + Boot.MAIN_JAR, Project.MSG_VERBOSE);
    } else {
      super.zipFile(is, zOut, vPath, lastModified, fromArchive, mode);
    }
  }
}
