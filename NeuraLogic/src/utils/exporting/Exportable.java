package utils.exporting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface Exportable<I> {

    default void export(Exporter exporter) {
        exporter.export(this);
    }

    default String exportToJson() {
        if (this == null) {
            return null;
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeSpecialFloatingPointValues()
                .create();
        String json = gson.toJson(this);
        return json;
    }
}