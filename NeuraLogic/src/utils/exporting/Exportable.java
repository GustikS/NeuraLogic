package utils.exporting;

public interface Exportable<I> {

    I export(Exporter exporter);

}