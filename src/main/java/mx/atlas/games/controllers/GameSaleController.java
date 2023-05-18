package mx.atlas.games.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;
import mx.atlas.games.db.Connection;
import mx.atlas.games.dtos.GameSale;
import mx.atlas.games.utils.Try;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.orderBy;

public class GameSaleController {

    @FXML
    public TextField rankingGreater;
    @FXML
    public TextField rankingLower;
    @FXML
    private TableView<GameSale> gameTable;
    @FXML
    private TableColumn<GameSale, String> columnTitle;
    @FXML
    public TableColumn<GameSale, Integer> columnRaking;
    @FXML
    public TableColumn<GameSale, Long> columnSales;
    @FXML
    public TableColumn<GameSale, String> columnSeries;
    @FXML
    public TableColumn<GameSale, String> columnPlatform;
    @FXML
    public TableColumn<GameSale, String> columnDevelopers;
    @FXML
    public TableColumn<GameSale, String> columnReleaseDate;
    @FXML
    public TableColumn<GameSale, String> columnPublishers;
    @FXML
    public Button updateButton;
    @FXML
    public Button duplicateButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Button insertButton;
    @FXML
    public ComboBox<String> cbPlatform;
    @FXML
    public ComboBox<String> cbDevelopers;
    @FXML
    public TextField tfTitle;
    public static String ALL_FIELDS = "Todos";

    private final ObservableMap<ObjectId, GameSale> editedRows = FXCollections.observableHashMap();

    private final MongoCollection<GameSale> collection = Connection.getDefaultCollection();

    public void init() {
        columnTitle.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getTitle()));
        columnRaking.setCellValueFactory(x -> new SimpleIntegerProperty(x.getValue().getRank()).asObject());
        columnSales.setCellValueFactory(x -> new SimpleLongProperty(x.getValue().getSales()).asObject());
        columnSeries.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getSeries()));
        columnPlatform.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getPlatforms()));
        columnDevelopers.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getDevelopers()));
        columnReleaseDate.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getReleaseDate()));
        columnPublishers.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getPublishers()));

        numericOnly(rankingGreater);
        numericOnly(rankingLower);

        BooleanBinding updateBinding = Bindings.createBooleanBinding(editedRows::isEmpty, editedRows);
        updateButton.disableProperty().bind(updateBinding);

        BooleanBinding selectedItemBinding = Bindings.isNull(gameTable.getSelectionModel().selectedItemProperty());
        deleteButton.disableProperty().bind(selectedItemBinding);
        duplicateButton.disableProperty().bind(selectedItemBinding);

        columnTitle.setCellFactory(TextFieldTableCell.forTableColumn());
        columnTitle.setOnEditCommit(event -> {
            GameSale rowValue = event.getRowValue();
            rowValue.setTitle(event.getNewValue());
            editedRows.put(rowValue.getId(), rowValue);
        });

        columnRaking.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        columnRaking.setOnEditCommit(event -> {
            GameSale rowValue = event.getRowValue();
            rowValue.setRank(event.getNewValue());
            editedRows.put(rowValue.getId(), rowValue);
        });

        columnSales.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        columnSales.setOnEditCommit(event -> {
            GameSale rowValue = event.getRowValue();
            rowValue.setSales(event.getNewValue());
            editedRows.put(rowValue.getId(), rowValue);
        });

        columnSeries.setCellFactory(TextFieldTableCell.forTableColumn());
        columnSeries.setOnEditCommit(event -> {
            GameSale rowValue = event.getRowValue();
            rowValue.setSeries(event.getNewValue());
            editedRows.put(rowValue.getId(), rowValue);
        });

        columnDevelopers.setCellFactory(TextFieldTableCell.forTableColumn());
        columnDevelopers.setOnEditCommit(event -> {
            GameSale rowValue = event.getRowValue();
            rowValue.setDevelopers(event.getNewValue());
            editedRows.put(rowValue.getId(), rowValue);
        });

        columnReleaseDate.setCellFactory(TextFieldTableCell.forTableColumn());
        columnReleaseDate.setOnEditCommit(event -> {
            GameSale rowValue = event.getRowValue();
            rowValue.setReleaseDate(event.getNewValue());
            editedRows.put(rowValue.getId(), rowValue);
        });

        columnPublishers.setCellFactory(TextFieldTableCell.forTableColumn());
        columnPublishers.setOnEditCommit(event -> {
            GameSale rowValue = event.getRowValue();
            rowValue.setPublishers(event.getNewValue());
            editedRows.put(rowValue.getId(), rowValue);
        });

        ObservableList<String> platformsFilter =
                collection.distinct("platforms", String.class)
                        .into(FXCollections.observableArrayList());

        columnPlatform.setCellFactory(ComboBoxTableCell.forTableColumn(platformsFilter));
        columnPlatform.setOnEditCommit(event -> {
            GameSale rowValue = event.getRowValue();
            rowValue.setPlatforms(event.getNewValue());
            editedRows.put(rowValue.getId(), rowValue);
        });

        updateButton.setOnAction(x -> updateAction());
        duplicateButton.setOnAction(x -> duplicateAction());
        deleteButton.setOnAction(x -> deleteAction());
        insertButton.setOnAction(x -> insertAction());

        ObservableList<String> platformFilterAll = FXCollections.observableArrayList();
        platformFilterAll.add(ALL_FIELDS);
        platformFilterAll.addAll(platformsFilter);
        cbPlatform.setItems(platformFilterAll);
        cbPlatform.setValue(platformFilterAll.get(0));
        cbPlatform.setOnAction(x -> applyFilter());

        ObservableList<String> developersFilter =
                collection.distinct("developers", String.class)
                        .into(FXCollections.observableArrayList());
        developersFilter.add(0, ALL_FIELDS);
        cbDevelopers.setItems(developersFilter);
        cbDevelopers.setValue(developersFilter.get(0));
        cbDevelopers.setOnAction(x -> applyFilter());
        tfTitle.setOnAction(x -> applyFilter());
        rankingGreater.setOnAction(x -> applyFilter());
        rankingLower.setOnAction(x -> applyFilter());

        loadTableData(collection);
    }

    private void applyFilter() {
        ArrayList<Bson> filters = new ArrayList<>();

        (cbPlatform.getValue().equals(ALL_FIELDS) ? Optional.<String>empty() : Optional.ofNullable(cbPlatform.getValue()))
                .map(z -> Filters.eq("platforms", z))
                .ifPresent(filters::add);
        (cbDevelopers.getValue().equals(ALL_FIELDS) ? Optional.<String>empty() : Optional.ofNullable(cbDevelopers.getValue()))
                .map(z -> Filters.eq("developers", z))
                .ifPresent(filters::add);

        (tfTitle.getText().isEmpty() ? Optional.<String>empty() : Optional.ofNullable(tfTitle.getText()))
                .map(value -> {
                    Pattern pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
                    return Filters.regex("title", pattern);
                }).ifPresent(filters::add);

        (rankingGreater.getText().isEmpty() ? Optional.<Integer>empty() : Try.of(() -> Integer.parseInt(rankingGreater.getText())).toOptional())
                .map(value -> Filters.gt("rank", value))
                .ifPresent(filters::add);

        (rankingLower.getText().isEmpty() ? Optional.<Integer>empty() : Try.of(() -> Integer.parseInt(rankingLower.getText())).toOptional())
                .map(value -> Filters.lt("rank", value))
                .ifPresent(filters::add);

        Bson filter = filters.isEmpty() ? new Document() : Filters.and(filters);

        ObservableList<GameSale> data = collection.find(filter)
                .sort(orderBy(ascending("rank")))
                .into(FXCollections.observableArrayList());

        gameTable.setItems(data);
        editedRows.clear();
    }

    private void loadTableData(MongoCollection<GameSale> collection) {
        ObservableList<GameSale> data = collection.find()
                .sort(orderBy(ascending("rank")))
                .into(FXCollections.observableArrayList());

        gameTable.setItems(data);
    }

    private void updateAction() {
        editedRows.forEach((objectId, gameSale) -> {
            if (gameSale.isInsert()) {
                collection.insertOne(gameSale);
                gameSale.setInsert(false);
            } else {
                collection.replaceOne(Filters.eq("_id", objectId), gameSale);
            }
        });
        editedRows.clear();
        applyFilter();
    }

    private void duplicateAction() {
        Optional.ofNullable(gameTable.getSelectionModel().getSelectedItem())
                .ifPresent(value -> {
                    GameSale copy = value.copy();
                    collection.insertOne(copy);
                    applyFilter();
                });
    }

    private void deleteAction() {
        Optional.ofNullable(gameTable.getSelectionModel().getSelectedItem())
                .ifPresent(value -> {
                    collection.deleteOne(Filters.eq("_id", value.getId()));
                    applyFilter();
                });
    }

    private void insertAction() {
        GameSale gameSale = new GameSale();
        gameSale.setId(new ObjectId());
        gameSale.setInsert(true);
        gameTable.getItems().add(gameSale);
        gameTable.requestFocus();
        gameTable.getSelectionModel().selectLast();
        gameTable.scrollTo(gameTable.getItems().size() - 1);
        gameTable.edit(gameTable.getItems().size() - 1, columnTitle);
    }

    public static void numericOnly(final TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

}