package ee.twentyten.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.ImageIcon;
import org.json.JSONObject;

public final class FileHelper {

  public static final long CACHE_EXPIRATION_TIME;
  private static final Class<FileHelper> CLASS_REF;
  public static File workingDirectory;

  static {
    CACHE_EXPIRATION_TIME = 86400000L;
    CLASS_REF = FileHelper.class;

    workingDirectory = LauncherHelper.getWorkingDirectory();
  }

  private FileHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static Image readImageFile(Class<?> clazz, String name) {
    URL input = clazz.getClassLoader().getResource(name);
    Objects.requireNonNull(input);
    try {
      LogHelper.logInfo(CLASS_REF, String.format("\"%s\"", input));
      return ImageIO.read(input);
    } catch (IOException ioe) {
      LogHelper.logError(CLASS_REF, "Failed to read image", ioe);
      return new ImageIcon(new byte[768]).getImage();
    }
  }

  public static JSONObject readJsonFile(File src) {
    if (!src.exists() || !src.isFile()) {
      Throwable t = new Throwable(String.format("File \"%s\" doesn't exist or isn't a file",
          src.getAbsolutePath()));

      LogHelper.logError(CLASS_REF, t.getCause().getMessage(), t);
      return null;
    }

    byte[] bytes;
    try {
      bytes = Files.readAllBytes(src.toPath());
    } catch (IOException ioe) {
      LogHelper.logError(CLASS_REF, "Failed to read bytes from file", ioe);
      return new JSONObject();
    }

    String json = new String(bytes, StandardCharsets.UTF_8);
    return new JSONObject(json);
  }

  public static void downloadFile(String url, File src) {
    HttpsURLConnection connection = RequestHelper.performRequest(url, "GET",
        RequestHelper.xWwwFormHeader, true);

    Objects.requireNonNull(connection, "connection == null!");
    try (InputStream is = connection.getInputStream()) {
      Files.copy(is, src.toPath());

      LogHelper.logInfo(CLASS_REF, String.format("\"%s\"", src.getAbsolutePath()));
    } catch (IOException ioe) {
      LogHelper.logError(CLASS_REF, "Failed to download file", ioe);
    }
  }

  public static File createDirectory(File parent, String name) {
    File directory = new File(parent, name);
    if (!directory.exists()) {
      boolean created = directory.mkdir();
      if (!created) {
        Throwable t = new Throwable("Failed to create directory");

        LogHelper.logError(CLASS_REF, t.getCause().getMessage(), t);
        return null;
      }
    }
    return directory;
  }

  public static void deleteDirectory(File directory) {
    if (directory.exists()) {
      File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            FileHelper.deleteDirectory(file);
          } else {
            boolean deleted = file.delete();
            if (!deleted) {
              Throwable t = new Throwable("Failed to delete file");

              LogHelper.logError(CLASS_REF, t.getCause().getMessage(), t);
              return;
            }
          }
        }
      }

      boolean deleted = directory.delete();
      if (!deleted) {
        Throwable t = new Throwable("Failed to delete directory");

        LogHelper.logError(CLASS_REF, t.getCause().getMessage(), t);
      }
    }
  }
}
