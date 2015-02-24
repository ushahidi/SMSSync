package org.addhen.smssync.util;

import org.addhen.smssync.listeners.LogListener;
import org.addhen.smssync.models.Log;
import org.addhen.smssync.state.LogEvent;

import android.os.Environment;
import android.text.format.DateFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Keep log of activities through out most part of the application
 *
 * Highly based on the AppLog file at https://github.com/jberkel/sms-backup-plus/blob/master/src/com/zegoggles/smssync/utils/AppLog.java
 */
public class LogUtil {

    private final static String TAG = LogUtil.class.getSimpleName();

    private PrintWriter writer;

    private String dateFormat;

    static final int MAX_SIZE = 32 * 1024;

    public static final int ID = 1;

    public static final String LOG_NAME = "smssync_log";

    private LogListener mLogListener;

    public LogUtil(char[] format) {
        this(LOG_NAME, format);
    }

    private LogUtil(String name, char[] format) {
        for (char c : format) {
            if (c == DateFormat.MONTH) {
                dateFormat = "MM-dd kk:mm";
                break;
            }

            if (c == DateFormat.DATE) {
                dateFormat = "dd-MM kk:mm";
                break;
            }
        }

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            final File logFile = getFile(name);
            if (logFile.isFile() && logFile.exists()) {
                rotate(logFile);
            }

            try {
                writer = new PrintWriter(new FileWriter(logFile, true));
            } catch (IOException e) {
                Logger.log(TAG, "error opening app log", e);
            }
        }

    }

    public CharSequence format(Date d) {
        return DateFormat.format(dateFormat, d);
    }


    public void append(String s) {
        if (writer != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(format(new Date()))
                    .append(" ").append(s);
            writer.println(sb);
            Logger.log(TAG, "Log " + sb);
        }
    }

    private void rotate(final File logFile) {
        if (logFile.length() > MAX_SIZE) {
            Logger.log(TAG, "rotating logfile " + logFile);
            new Thread() {
                @Override
                public void run() {
                    try {
                        LineNumberReader r = new LineNumberReader(new FileReader(logFile));

                        while (r.readLine() != null) {
                            ;
                        }
                        r.close();

                        int keep = Math.round(r.getLineNumber() * 0.3f);
                        if (keep > 0) {
                            r = new LineNumberReader(new FileReader(logFile));

                            while (r.readLine() != null && r.getLineNumber() < keep) {
                                ;
                            }

                            File newFile = new File(logFile.getAbsolutePath() + ".new");
                            PrintWriter pw = new PrintWriter(new FileWriter(newFile));
                            String line;
                            while ((line = r.readLine()) != null) {
                                pw.println(line);
                            }

                            pw.close();
                            r.close();

                            if (newFile.renameTo(logFile)) {
                                Logger.log(TAG, "rotated file, new size = " + logFile.length());
                            }
                        }
                    } catch (IOException e) {
                        Logger.log(TAG, "error rotating file " + logFile, e);
                    }
                }
            }.start();
        }
    }

    /**
     * Get the name of a file.
     *
     * @param name The name of the file.
     * @return The log file.
     */
    public static File getFile(String name) {
        return new File(Environment.getExternalStorageDirectory(), name);
    }

    /**
     * Read log files
     *
     * @param name the name of the log file.
     * @return the list of log files
     */
    public static List<Log> readLogFile(String name) {
        return readLogFile(getFile(name));
    }

    /**
     * Read the lines of a log file
     *
     * @param file The file to read
     * @return List of log lines
     */
    public static List<Log> readLogFile(final File file) {
        List<Log> logs = null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<Log>> result = executorService.submit(new Callable<List<Log>>() {
            @Override
            public List<Log> call() throws Exception {
                List<Log> logger = new ArrayList<>();
                if (file.exists()) {
                    BufferedReader bufferedReader = null;
                    try {
                        bufferedReader = new BufferedReader(new FileReader(file));
                        String fileLine;
                        while ((fileLine = bufferedReader.readLine()) != null) {
                            Log log = new Log();
                            log.setMessage(fileLine);
                            logger.add(log);
                        }
                    } catch (IOException e) {
                        Logger.log(TAG, "Error reading log file", e);
                    } finally {
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                return logger;
            }

        });
        try {
            executorService.shutdown();
           return result.get();
        }catch(Exception e) {
            return logs;
        }

    }

    public static String readLogs(String name) {
        return readLogs(getFile(name));
    }

    /**
     * The logs files
     *
     * @param file The log file to delete.
     * @return The read log entries.
     */
    public static String readLogs(final File file) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> result = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                StringBuffer logs = new StringBuffer();
                if (file.exists()) {
                    BufferedReader bufferedReader = null;
                    try {
                        bufferedReader = new BufferedReader(new FileReader(file));
                        String fileLine;
                        while ((fileLine = bufferedReader.readLine()) != null) {
                            logs.append(fileLine);
                            logs.append("\n");
                        }
                    } catch (IOException e) {
                        Logger.log(TAG, "Error reading log file", e);
                    } finally {
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                return logs.toString();
            }
        });

        try {
            executorService.shutdown();
            return result.get();
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Append a new line to an existing file.
     *
     * @param line The line to append to the file.
     */
    public void appendAndClose(String line, LogListener listener) {
        append(line);
        close();
        mLogListener = listener;
        mLogListener.reloadLog(new LogEvent());
    }

    public void appendAndClose(String line) {
        appendAndClose(line, new LogListener() {
            @Override
            public void reloadLog(LogEvent event) {

            }
        });
    }

    /**
     * Close all opened resources.
     */
    public void close() {
        Logger.log(TAG, "CloseLog");
        if (writer != null) {
            writer.close();
        }
    }

    /**
     * Delete log with a given file name
     *
     * @param name The log file to delete.
     * @return the status of the delete action. true/false
     */
    public static boolean deleteLog(String name) {
        return deleteLog(getFile(name));
    }

    /**
     * Delete a file.
     *
     * @param file The file to delete.
     * @return the status of the delete action. true/false
     */
    public static boolean deleteLog(final File file) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> result = executorService.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                if (file.exists()) {
                    return file.delete();
                }
                return false;
            }
        });

        try {
            executorService.shutdown();
            return result.get();
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
