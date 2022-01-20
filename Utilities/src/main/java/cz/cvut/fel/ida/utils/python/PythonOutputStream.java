package cz.cvut.fel.ida.utils.python;

import java.io.IOException;
import java.io.OutputStream;

public class PythonOutputStream extends OutputStream {
    public interface TextIOWrapper {
        void write(String text);
    }

    private final TextIOWrapper textIOWrapper;
    private StringBuilder stringBuilder = new StringBuilder();

    public PythonOutputStream(TextIOWrapper textIOWrapper) {
        this.textIOWrapper = textIOWrapper;
    }

    @Override
    public void write(int i) throws IOException {
        stringBuilder.append((char) i);

        if (i == '\n') {
            textIOWrapper.write(stringBuilder.toString());
            stringBuilder = new StringBuilder();
        }
    }
}
