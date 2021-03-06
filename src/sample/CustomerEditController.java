package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerEditController {

    public TextField customerNameField, customerContactField;
    public TextArea customerAddressField;
    public String selectedCityId;
    public int customerId;
    Connection connect = new Connection();

    ObservableList<ModelTableCity> oblist = FXCollections.observableArrayList();

    @FXML
    private TableView<ModelTableCity> cityTable;


    public void getCity(){
        ModelTableCity city = cityTable.getSelectionModel().getSelectedItem();
        selectedCityId = String.valueOf(city.getCityId());
    }


    public void setCustomerId(Integer customerId){
        this.customerId = customerId;
    }


    public void saveButton() throws SQLException {

        if (customerAddressField.getText().isEmpty() || customerContactField.getText().isEmpty() || customerNameField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Something Wrong!");
            alert.setHeaderText("Check your input");
            alert.setContentText("Make sure you fill all field with correct value");
            alert.show();
        } else{
            getCity();
            PreparedStatement prepStat = connect.getPrepStat("UPDATE Customer SET customerName = '" + customerNameField.getText() + "', customerAddress = '" + customerAddressField.getText() + "', customerContact = '" + customerContactField.getText() + "', cityId = " + selectedCityId + " WHERE customerId = " + customerId + ";");
            prepStat.executeUpdate();
            Stage closeWindow = (Stage) customerNameField.getScene().getWindow();
            closeWindow.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setContentText("Save Successful!");
            alert.setHeaderText("SAVED");

            alert.show();
        }
    }


        public int getCityIndex() throws SQLException {
            int cityId = 0;

            try{
            PreparedStatement prepStat = connect.getPrepStat("SELECT cityId FROM Customer WHERE CustomerId = " + customerId + ";");
            ResultSet rs = prepStat.executeQuery();

            if(rs.next()){
                cityId = rs.getInt("cityId");
            }

            for (int i = 0; i < cityTable.getItems().size(); i++) {
                if (cityTable.getItems().get(i).getCityId() == cityId) {
                    return i;
                }
            }
            return 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }


    public void preselectCityAndOthers() throws SQLException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cityTable.requestFocus();
                try {
                    cityTable.getSelectionModel().select(getCityIndex());
                    cityTable.getFocusModel().focus(getCityIndex());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        PreparedStatement prepStat = connect.getPrepStat("SELECT customerName,customerAddress, customerContact " +
                "FROM Customer WHERE customerId = " + customerId + ";");
        ResultSet rs = prepStat.executeQuery();

        if(rs.next()){
            customerNameField.setText(rs.getString("customerName"));
            customerAddressField.setText(rs.getString("customerAddress"));
            customerContactField.setText(rs.getString("customerContact"));
        }
    }


    public void showTable(){

        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT cityId, city, province FROM City, Province WHERE City.provinceId = Province.provinceId;");
            ResultSet rs = prepStat.executeQuery();

            while (rs.next()) {
                oblist.add(new ModelTableCity(rs.getInt("cityId"), rs.getString("city"), rs.getString("province")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadFirst() {
        showTable();

        try {
            preselectCityAndOthers();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TableColumn movCol = new TableColumn("City");
        movCol.setMinWidth(200);
        movCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCity, String>("city"));

        TableColumn ratCol = new TableColumn("Province");
        ratCol.setMinWidth(200);
        ratCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCity, String>("province"));
        cityTable.setItems(oblist);
        cityTable.getColumns().addAll(movCol, ratCol);
    }
}
