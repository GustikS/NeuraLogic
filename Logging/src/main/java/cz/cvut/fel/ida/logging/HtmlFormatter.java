package cz.cvut.fel.ida.logging;

/**
 * Created by gusta on 27.2.18.
 */
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

// this custom formatter formats pipes of a log record to a single line
class HtmlFormatter extends Formatter {
    // this method is called for every log record
    public String format(LogRecord rec) {
        StringBuilder buf = new StringBuilder(1000);
        buf.append("<tr>\n");

        // colorize any levels >= WARNING in red
        if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
            buf.append("\t<td style=\"color:red\">");
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        } else {
            buf.append("\t<td>");
            buf.append(rec.getLevel());
        }

        buf.append("</td>\n");
        buf.append("\t<td>");
        buf.append(Logging.calcTime(rec.getMillis()));
        buf.append("</td>\n");
        buf.append("\t<td>");
        buf.append(formatMessage(rec));
        buf.append("</td>\n");
        buf.append("</tr>\n");

        return buf.toString();
    }
}