package cz.cvut.fel.ida.utils.exporting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

public interface Exportable<I> extends Serializable {

    default void export(Exporter exporter) {
        exporter.export(this);
    }

    default String exportToJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeSpecialFloatingPointValues()
                .create();
        String json = gson.toJson(this);
        return json;
    }
}