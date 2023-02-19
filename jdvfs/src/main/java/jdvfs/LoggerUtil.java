package jdvfs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** Helper for logging. */
final class LoggerUtil {
  private static final SimpleDateFormat dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z");

  private static boolean isInitialized = false;

  static synchronized Logger getLogger() {
    if (!isInitialized) {
      ConsoleHandler handler = new ConsoleHandler();
      handler.setFormatter(
          new Formatter() {
            @Override
            public String format(LogRecord record) {
              return String.join(
                  " ",
                  makePrefix(new Date(record.getMillis())),
                  record.getMessage(),
                  System.lineSeparator());
            }
          });

      Logger logger = Logger.getLogger("jdvfs");
      logger.setUseParentHandlers(false);

      for (Handler hdlr : logger.getHandlers()) {
        logger.removeHandler(hdlr);
      }
      logger.addHandler(handler);
      isInitialized = true;

      return logger;
    } else {
      return Logger.getLogger("jdvfs");
    }
  }

  private static String makePrefix(Date date) {
    return String.join(
        " ",
        "jdvfs",
        "(" + dateFormatter.format(date) + ")",
        "[" + Thread.currentThread().getName() + "]:");
  }

  private LoggerUtil() {}
}