package org.addhen.smssync.data.cache;

import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.entity.Log;
import org.addhen.smssync.data.exception.LogNotFoundException;
import org.addhen.smssync.data.util.Logger;

import android.content.Context;
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

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class FileManager {

    public static final String LOG_NAME = "smssync_log";

    static final int MAX_SIZE = 32 * 1024;

    private final static String TAG = FileManager.class.getSimpleName();

    private PrintWriter mWriter;

    private String mDateFormat;

    private String mName;

    PrefsFactory mPrefsFactory;

    @Inject
    public FileManager(Context context, PrefsFactory prefsFactory) {
        this(LOG_NAME);
        mPrefsFactory = prefsFactory;
    }

    public FileManager(String name) {
        mName = name;
        mDateFormat = "dd-MM kk:mm";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            final File logFile = getFile(name);
            if (logFile.isFile() && logFile.exists()) {
                rotate(logFile);
            }

            try {
                mWriter = new PrintWriter(new FileWriter(logFile, true));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
     * Read the lines of a log file
     *
     * @param file The file to read
     * @return List of log lines
     */
    public static List<Log> readLogFile(File file) {
        List<Log> logs = new ArrayList<>();
        if (file.exists()) {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                String fileLine;
                while ((fileLine = bufferedReader.readLine()) != null) {
                    Log log = new Log();
                    log.setMessage(fileLine);
                    logs.add(log);
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
        return logs;
    }

    public String readLogs(String name) {
        return readLogs(getFile(name));
    }

    /**
     * The logs files
     *
     * @param file The log file to delete.
     * @return The read log entries.
     */
    public String readLogs(File file) {
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

    /**
     * Delete log with a given file name
     *
     * @param name The log file to delete.
     * @return the status of the delete action. true/false
     */
    public boolean deleteLog(String name) {
        return deleteLog(getFile(name));
    }

    /**
     * Delete a file.
     *
     * @param file The file to delete.
     * @return the status of the delete action. true/false
     */
    public boolean deleteLog(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public CharSequence format(Date d) {
        return DateFormat.format(mDateFormat, d);
    }

    public void append(String s) {
        if (mWriter != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(format(new Date()))
                    .append(" ").append(s);
            mWriter.println(sb);
            Logger.log(TAG, "Log " + sb);
        }
    }

    /**
     * Append a new line to an existing file.
     *
     * @param line The line to append to the file.
     */
    public void appendAndClose(String line) {
        if (mPrefsFactory.enableLog().get()) {
            append(line);
            close();
        }
    }

    /**
     * Close all opened resources.
     */
    public void close() {
        Logger.log(TAG, "CloseLog");
        if (mWriter != null) {
            mWriter.close();
        }
    }

    public Observable<List<Log>> getLogs() {
        return Observable.defer(() -> {
            final List<Log> logs = readLogFile(getFile(mName));
            if (logs != null) {
                return Observable.just(logs);
            } else {
                return Observable.error(new LogNotFoundException());
            }
        });
    }

    public Observable<Long> addLog(Log log) {
        return Observable.create(subscriber -> {
            appendAndClose(log.getMessage());
            subscriber.onNext(1l);
            subscriber.onCompleted();
        });
    }

    public Observable<Long> deleteLog() {
        return Observable.create(subscriber -> {
            deleteLog(mName);
            subscriber.onNext(1l);
            subscriber.onCompleted();
        });
    }

    public Observable<Log> getLog() {
        return Observable.create(subscriber -> {
            final String logString = readLogs(mName);
            if (logString != null) {
                Log log = new Log();
                log.setMessage(logString);
                subscriber.onNext(log);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new LogNotFoundException());
            }
        });
    }
}
