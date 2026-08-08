package cz.cvut.fel.ida.utils.exporting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.io.Serializable;
import java.time.Duration;

public interface Exportable<I> extends Serializable {

    default void export(Exporter exporter) {
        exporter.export(this);
    }

    default String exportToJson() {
        JsonSerializer<Duration> durationJsonSerializer = (duration, type, jsonSerializationContext) -> {
            JsonObject jsonDuration = new JsonObject();
            jsonDuration.addProperty("seconds", duration.getSeconds());
            jsonDuration.addProperty("nanos", duration.getNano());
            return jsonDuration;
        };

        //Gson would otherwise write a File through its private fields, which the module system refuses since
        //JDK 16. A File is its path; JsonExporter.fileAwareGson reads this shape back.
        JsonSerializer<File> fileJsonSerializer = (file, type, jsonSerializationContext) -> {
            JsonObject jsonFile = new JsonObject();
            jsonFile.addProperty("path", file.getPath());
            return jsonFile;
        };

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, durationJsonSerializer)
                .registerTypeAdapter(File.class, fileJsonSerializer)
                .serializeSpecialFloatingPointValues()
                .create();
        String json = gson.toJson(this);
        return json;
    }
}