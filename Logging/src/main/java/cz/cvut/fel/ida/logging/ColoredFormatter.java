package cz.cvut.fel.ida.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ColoredFormatter extends Formatter {
    private static final Logger LOG = Logger.getLogger(ColoredFormatter.class.getName());
    // ANSI escape code
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private long finestTimer = System.currentTimeMillis();
    private long finerTimer = System.currentTimeMillis();
    private long fineTimer = System.currentTimeMillis();
    private long infoTimer = System.currentTimeMillis();
    private long warningTimer = System.currentTimeMillis();
    private long severeTimer = System.currentTimeMillis();

    // format is called for every console log message
    @Override
    public String format(LogRecord record)
    {
        // This example will print date/time, class, and log level in yellow,
        // followed by the log message and it's parameters in white .
        StringBuilder builder = new StringBuilder();
        Level level = record.getLevel();

        long clock = record.getMillis();
        long delta = 0;

        switch (level.getName()){
            case "FINEST":
                delta = clock - finestTimer;
                finestTimer = clock;
                builder.append(ANSI_YELLOW);
                break;
            case "FINER":
                delta = clock - finerTimer;
                finerTimer = clock;
                builder.append(ANSI_CYAN);
                break;
            case "FINE":
                delta = clock - fineTimer;
                fineTimer = clock;
                builder.append(ANSI_BLUE);
                break;
            case "INFO":
                delta = clock - infoTimer;
                infoTimer = clock;
                builder.append(ANSI_BLACK);
                break;
            case "WARNING":
                delta = clock - warningTimer;
                warningTimer = clock;
                builder.append(ANSI_PURPLE);
                break;
            case "SEVERE":
                delta = clock - severeTimer;
                severeTimer = clock;
                builder.append(ANSI_RED);
                break;
        }

        builder.append("+").append(delta).append("ms - ");

        builder.append(Logging.calcDate(record.getMillis()));

        builder.append(" <");
        builder.append(record.getSourceClassName());
        builder.append(">");

        builder.append(" (");
        builder.append(record.getSourceMethodName());
        builder.append(")");

        builder.append(" [");
        builder.append(record.getLevel().getName());
        builder.append("]");

        //builder.append(ANSI_BLACK);
        builder.append(" - ");
        builder.append(record.getMessage());

        Object[] params = record.getParameters();

        if (params != null)
        {
            builder.append("\t");
            for (int i = 0; i < params.length; i++)
            {
                builder.append(params[i]);
                if (i < params.length - 1)
                    builder.append(", ");
            }
        }

        builder.append(ANSI_RESET);
        builder.append("\n");
        return builder.toString();
    }
}
