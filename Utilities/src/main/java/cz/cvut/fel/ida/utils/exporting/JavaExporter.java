package cz.cvut.fel.ida.utils.exporting;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static cz.cvut.fel.ida.utils.exporting.TextExporter.createFile;

public class JavaExporter extends Exporter {
    private static final Logger LOG = Logger.getLogger(JavaExporter.class.getName());

    FileOutputStream file;
    ObjectOutputStream out;

    public JavaExporter(String exportDir, String id) {
        super(exportDir, id, "JAVA");
    }

    public JavaExporter() {
        super();
    }

    @Override
    public void export(Exportable iExportable) {
        createFile(exportFile);
        try {
            //Saving of object in a file
            if (out == null) {
                file = new FileOutputStream(exportFile, true);
                out = new ObjectOutputStream(file);
            }
            out.writeObject(iExportable);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finish() {
        try {
            out.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <I> List<I> importListFrom(Path path, Class<I> cls) {
        try {
            FileInputStream myFileInputStream = new FileInputStream(String.valueOf(path));
            ObjectInputStream in = new ObjectInputStream(myFileInputStream);
            List<I> list = new ArrayList<>();
            I tmp;
            while (myFileInputStream.available() > 0 && (tmp = (I) in.readObject()) != null) {
                list.add(tmp);
            }
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <I> I importObjectFrom(Path path, Class<I> cls) {
        try {
            FileInputStream myFileInputStream = new FileInputStream(path.toString());
            ObjectInputStream in = new ObjectInputStream(myFileInputStream);
            I tmp = (I) in.readObject();
            return tmp;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delimitNext() {
        //nothing needed
    }

    @Override
    public void delimitStart() {
        //nothing needed
    }

    @Override
    public void delimitEnd() {
        //nothing needed
    }
}
