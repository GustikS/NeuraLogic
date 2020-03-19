package cz.cvut.fel.ida.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class NormalFormatter extends Formatter {
    private static final Logger LOG = Logger.getLogger(ColoredFormatter.class.getName());
    // ANSI escape code

    private long finestTimer = System.currentTimeMillis();
    private long finerTimer = System.currentTimeMillis();
    private long fineTimer = System.currentTimeMillis();
    private long infoTimer = System.currentTimeMillis();
    private long warningTimer = System.currentTimeMillis();
    private long severeTimer = System.currentTimeMillis();

    // format is called for every console log message
    @Override
    public String format(LogRecord record) {
        // This example will print date/time, class, and log level in yellow,
        // followed by the log message and it's parameters in white .
        StringBuilder builder = new StringBuilder();
        Level level = record.getLevel();

        long clock = record.getMillis();
        long delta = 0;

        switch (level.getName()) {
            case "FINEST":
                delta = clock - finestTimer;
                finestTimer = clock;
                break;
            case "FINER":
                delta = clock - finerTimer;
                finerTimer = clock;
                break;
            case "FINE":
                delta = clock - fineTimer;
                fineTimer = clock;
                break;
            case "INFO":
                delta = clock - infoTimer;
                infoTimer = clock;
                break;
            case "WARNING":
                delta = clock - warningTimer;
                warningTimer = clock;
                break;
            case "SEVERE":
                delta = clock - severeTimer;
                severeTimer = clock;
                break;
        }

        builder.append("+").append(delta).append("ms - ");

        builder.append(Logging.calcTime(record.getMillis()));

        builder.append(" <");
        builder.append(record.getSourceClassName());
        builder.append(">");

        builder.append(" (");
        builder.append(record.getSourceMethodName());
        builder.append(")");

        builder.append(" [");
        builder.append(record.getLevel().getName());
        builder.append("]");

        builder.append(" - ");
        builder.append(record.getMessage());

        Object[] params = record.getParameters();

        if (params != null) {
            builder.append("\t");
            for (int i = 0; i < params.length; i++) {
                builder.append(params[i]);
                if (i < params.length - 1)
                    builder.append(", ");
            }
        }

        builder.append("\n");
        return builder.toString();
    }
}
