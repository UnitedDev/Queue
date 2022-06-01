package fr.kohei.queue.independent.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static void print(String message) {
        System.out.println("[" + DATE_FORMAT.format(Calendar.getInstance().getTime()) + "] [Portal] " + message);
    }

}
