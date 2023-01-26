package ee.twentyten.utils;

import ee.twentyten.core.EPlatform;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class FilesManager {

  private static final String USER_HOME;
  private static final String APPDATA;
  private static final Map<EPlatform, File> GAME_DIRECTORIES;

  static {
    USER_HOME = System.getProperty("user.home", ".");
    APPDATA = System.getenv("APPDATA");

    GAME_DIRECTORIES = new HashMap<>();
    FilesManager.getGameDirectoriesForPlatform();
  }

  private FilesManager() {
  }

  public static File getGameDirectory() {
    EPlatform platformName = EPlatform.getPlatform();

    File gameDirectory = FilesManager.GAME_DIRECTORIES.get(platformName);
    Objects.requireNonNull(gameDirectory, "gameDirectory == null!");
    if (!gameDirectory.mkdirs() && !gameDirectory.exists()) {
      throw new RuntimeException("Can't create the game directory!");
    }
    return gameDirectory;
  }

  private static void getGameDirectoriesForPlatform() {
    File gameDirectoryForOsx = new File(String.format("%s/Library/Application Support", USER_HOME),
        "twentyten");
    GAME_DIRECTORIES.put(EPlatform.OSX, gameDirectoryForOsx);

    File gameDirectoryForLinux = new File(USER_HOME, ".twentyten");
    GAME_DIRECTORIES.put(EPlatform.LINUX, gameDirectoryForLinux);

    File gameDirectoryForWindows =
        APPDATA != null ? new File(APPDATA, ".twentyten") : new File(USER_HOME, ".twentyten");
    GAME_DIRECTORIES.put(EPlatform.WINDOWS, gameDirectoryForWindows);
  }
}
