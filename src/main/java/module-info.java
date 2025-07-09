module com.gjbmloslos.markovchainsimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.gjbmloslos.markovchainsimulator to javafx.fxml;
    exports com.gjbmloslos.markovchainsimulator;
}