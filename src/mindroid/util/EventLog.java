/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2013 Daniel Himmelein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mindroid.util;

/**
 * Mindroid event logger.
 * 
 * <p>
 * Generally, use the EventLog.v() EventLog.d() EventLog.i() EventLog.w() and EventLog.e() methods.
 * 
 * <p>
 * The order in terms of verbosity, from least to most is ERROR, WARN, INFO, DEBUG, VERBOSE. Verbose
 * should never be compiled into an application except during development. Debug logs are compiled
 * in but stripped at runtime. Error, warning and info logs are always kept.
 * 
 * <p>
 * <b>Tip:</b> A good convention is to declare a <code>LOG_TAG</code> constant in your class:
 * 
 * <pre>
 * private static final String LOG_TAG = &quot;MyService&quot;;
 * </pre>
 * 
 * and use that in subsequent calls to the log methods.
 * </p>
 * 
 * <p>
 * <b>Tip:</b> Don't forget that when you make a call like
 * 
 * <pre>
 * EventLog.v(LOG_TAG, &quot;index=&quot; + i);
 * </pre>
 * 
 * that when you're building the string to pass into EventLog.d, the compiler uses a StringBuilder and at
 * least three allocations occur: the StringBuilder itself, the buffer, and the String object.
 * Realistically, there is also another buffer allocation and copy, and even more pressure on the
 * gc. That means that if your log message is filtered out, you might be doing significant work and
 * incurring significant overhead.
 */
public final class EventLog {
    private EventLog() {
    }

    /**
     * Send a {@link #VERBOSE} log message.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int v(String tag, String msg) {
        return Log.println(Log.LOG_ID_EVENTS, Log.VERBOSE, tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int v(String tag, String msg, Throwable tr) {
        return Log.println(Log.LOG_ID_EVENTS, Log.VERBOSE, tag, msg + " \n" + Log.getStackTraceString(tr));
    }

    /**
     * Send a {@link #DEBUG} log message.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int d(String tag, String msg) {
        return Log.println(Log.LOG_ID_EVENTS, Log.DEBUG, tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int d(String tag, String msg, Throwable tr) {
        return Log.println(Log.LOG_ID_EVENTS, Log.DEBUG, tag, msg + " \n" + Log.getStackTraceString(tr));
    }

    /**
     * Send an {@link #INFO} log message.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int i(String tag, String msg) {
        return Log.println(Log.LOG_ID_EVENTS, Log.INFO, tag, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int i(String tag, String msg, Throwable tr) {
        return Log.println(Log.LOG_ID_EVENTS, Log.INFO, tag, msg + " \n" + Log.getStackTraceString(tr));
    }

    /**
     * Send a {@link #WARN} log message.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int w(String tag, String msg) {
        return Log.println(Log.LOG_ID_EVENTS, Log.WARN, tag, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int w(String tag, String msg, Throwable tr) {
        return Log.println(Log.LOG_ID_EVENTS, Log.WARN, tag, msg + " \n" + Log.getStackTraceString(tr));
    }

    /*
     * Send a {@link #WARN} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * 
     * @param tr An exception to log
     */
    public static int w(String tag, Throwable tr) {
        return Log.println(Log.LOG_ID_EVENTS, Log.WARN, tag, Log.getStackTraceString(tr));
    }

    /**
     * Send an {@link #ERROR} log message.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int e(String tag, String msg) {
        return Log.println(Log.LOG_ID_EVENTS, Log.ERROR, tag, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     * 
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     * activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int e(String tag, String msg, Throwable tr) {
        return Log.println(Log.LOG_ID_EVENTS, Log.ERROR, tag, msg + " \n" + Log.getStackTraceString(tr));
    }

    /**
     * What a Terrible Failure: Report a condition that should never happen. The error will always
     * be logged at level ASSERT with the call stack. Depending on system configuration, a report
     * may be added to the {@link android.os.DropBoxManager} and/or the process may be terminated
     * immediately with an error dialog.
     * 
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    public static int wtf(String tag, String msg) {
        return Log.println(Log.LOG_ID_EVENTS, Log.WTF, tag, msg);
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen. Similar to
     * {@link #wtf(String, String)}, with an exception to log.
     * 
     * @param tag Used to identify the source of a log message.
     * @param tr An exception to log.
     */
    public static int wtf(String tag, Throwable tr) {
        return Log.println(Log.LOG_ID_EVENTS, Log.WTF, tag, Log.getStackTraceString(tr));
    }

    public static int wtf(String tag, String msg, Throwable tr) {
        return Log.println(Log.LOG_ID_EVENTS, Log.WTF, tag, msg + " \n" + Log.getStackTraceString(tr));
    }
}
