package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UnpaidController implements Initializable {
    public String salesId;
    Connection connect = new Connection();

    ObservableList<ModelTableUnpaid> oblist = FXCollections.observableArrayList();

    @FXML
    private TableView<ModelTableUnpaid> unpaidTable;

    public void showUnpaidTable(){
        try{
            PreparedStatement prepStat = connect.getPrepStat("SELECT salesId, customerId, salesDate, subTotal, " +
                    "paid FROM Sales WHERE paid != subTotal;");
            ResultSet rs = prepStat.executeQuery();

            while (rs.next()) {
                oblist.add(new ModelTableUnpaid(String.valueOf(rs.getInt("salesID")),
                        String.valueOf(rs.getInt("customerId")), rs.getString("salesDate"),
                        String.valueOf(rs.getInt("subTotal")), String.valueOf(rs.getInt("paid"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCustomerId() {
        ModelTableUnpaid unpaid = unpaidTable.getSelectionModel().getSelectedItem();
        salesId = unpaid.getSalesId();
    }

    public void paidButton() {
        try{
            PreparedStatement prepStat = connect.getPrepStat("UPDATE Sales SET paid = subTotal WHERE salesId = " + salesId);
            prepStat.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Paid!");

            alert.showAndWait();

            Stage closeWindow = (Stage) unpaidTable.getScene().getWindow();
            closeWindow.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showUnpaidTable();

        TableColumn idCol = new TableColumn("ID");
        idCol.setMinWidth(50);
        idCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCity, String>("customerId"));

        TableColumn dateCol = new TableColumn("Date");
        dateCol.setMinWidth(100);
        dateCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCity, String>("salesDate"));

        TableColumn subCol = new TableColumn("Subtotal");
        subCol.setMinWidth(150);
        subCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCity, String>("subTotal"));

        TableColumn paidCol = new TableColumn("Paid");
        paidCol.setMinWidth(150);
        paidCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCity, String>("paid"));
        unpaidTable.setItems(oblist);
        unpaidTable.getColumns().addAll(idCol, dateCol, subCol, paidCol);
    }
}
