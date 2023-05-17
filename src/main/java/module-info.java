module mx.atlas.games {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires java.logging;
    requires static lombok;

    opens mx.atlas.games to javafx.fxml;
    exports mx.atlas.games;
    exports mx.atlas.games.dtos to org.mongodb.bson;
    exports mx.atlas.games.controllers;
    opens mx.atlas.games.controllers to javafx.fxml;
}
