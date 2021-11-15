package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BusinessUtil {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy hh:mm a");

    public static String convertStackTraceToString(Throwable throwable)
    {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw))
        {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
        catch (IOException ioe)
        {
            throw new IllegalStateException(ioe);
        }
    }

    public static String getDateString(LocalDate d){
        if(d != null) {
            return d.format(dateFormatter);
        }
        return null;
    }

    public static String getDateTimeString(LocalDateTime d){
        if(d != null) {
            return d.format(timeFormatter);
        }
        return null;
    }
}
