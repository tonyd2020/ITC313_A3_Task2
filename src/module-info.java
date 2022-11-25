module HelloFX2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;

    opens org.openjfx to javafx.fxml;

    exports org.openjfx;
}