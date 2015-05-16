/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.util;

import android.os.Environment;
import android.text.format.DateFormat;

import org.addhen.smssync.listeners.LogListener;
import org.addhen.smssync.models.Log;

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

/**
 * Keep log of activities through out most part of the application
 * <p/>
 * Highly based on the AppLog file at https://github.com/jberkel/sms-backup-plus/blob/master/src/com/zegoggles/smssync/utils/AppLog.java
 */
public class LogUtil {

    public static final int ID = 1;
    public static final String LOG_NAME = "smssync_log";
    static final int MAX_SIZE = 32 * 1024;
    private final static String TAG = LogUtil.class.getSimpleName();
    private PrintWriter writer;
    private String dateFormat;
    private LogListener mLogListener;

    public LogUtil(char[] format) {
        this(LOG_NAME, format);
    }

    private LogUtil(String name, char[] format) {
        for (char c : format) {
            if (c == 'M') {
                dateFormat = "MM-dd kk:mm";
                break;
            }

            if (c == 'd') {
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
    public static List<Log> readLogFile(File file) {
        List<Log> logs = new ArrayList<Log>();
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

    public static String readLogs(String name) {
        return readLogs(getFile(name));
    }

    /**
     * The logs files
     *
     * @param file The log file to delete.
     * @return The read log entries.
     */
    public static String readLogs(File file) {
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
    public static boolean deleteLog(String name) {
        return deleteLog(getFile(name));
    }

    /**
     * Delete a file.
     *
     * @param file The file to delete.
     * @return the status of the delete action. true/false
     */
    public static boolean deleteLog(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return false;
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
     * Append a new line to an existing file.
     *
     * @param line The line to append to the file.
     */
    public void appendAndClose(String line) {
        append(line);
        close();
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
}
