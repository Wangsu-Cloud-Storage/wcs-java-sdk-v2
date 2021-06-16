package com.wos.log;

import java.lang.reflect.Method;

public class LoggerBuilder {

    static class GetLoggerHolder {
        static Class<?> logManagerClass;
        static Class<?> loggerClass;
        static Method getLoggerClass;

        static {
            try {
                logManagerClass = Class.forName("org.apache.logging.log4j.LogManager");
                loggerClass = Class.forName("org.apache.logging.log4j.Logger");
                getLoggerClass = GetLoggerHolder.logManagerClass.getMethod("getLogger", String.class);
            } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
                try {
                    loggerClass = Class.forName("org.apache.log4j.Logger");
                    getLoggerClass = GetLoggerHolder.loggerClass.getMethod("getLogger", String.class);
                } catch (NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
                    try {
                        loggerClass = Class.forName("java.util.logging.Logger");
                        getLoggerClass = GetLoggerHolder.loggerClass.getMethod("getLogger", String.class);
                    } catch (NoSuchMethodException | SecurityException | ClassNotFoundException exx) {

                    }
                }
            }
        }
    }

    public static ILogger getLogger(String name) {
        if (GetLoggerHolder.getLoggerClass != null) {
            try {
                return new Logger(((Method) GetLoggerHolder.getLoggerClass).invoke(null, name));
            } catch (Exception e) {
            }
        }
        return new Logger(null);
    }

    public static ILogger getLogger(Class<?> c) {
        return getLogger(c.getName());
    }
}
