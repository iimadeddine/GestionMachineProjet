/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestion_machine2;

import classe.Machine;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.hibernate.PropertyValueException;
import service.MachineService;

/**
 *
 * @author imadeddine
 */
public class FXMLDocumentController implements Initializable {

    MachineService ms = new MachineService();
    ObservableList<Machine> machineList = FXCollections.observableArrayList();
    private static int index;
    Date dt = new Date();

    @FXML
    private TextField marque;

    @FXML
    private TextField reference;

    @FXML
    private DatePicker dateAchat;

    @FXML
    private TextField prix;

    @FXML
    private TableView<Machine> machines;

    @FXML
    private TableColumn<Machine, String> cMarque;
    @FXML
    private TableColumn<Machine, String> cReference;
    @FXML
    private TableColumn<Machine, LocalDate> cDateAchat;
    @FXML
    private TableColumn<Machine, String> cPrix;

    @FXML
    private void saveAction(ActionEvent event) {
        String m = marque.getText().toString();
        String r = reference.getText().toString();
        LocalDate d = dateAchat.getValue();
        String p = prix.getText().toString();
        Instant instant = Instant.from(d.atStartOfDay(ZoneId.systemDefault()));
        dt = Date.from(instant);

        ms.create(new Machine(m, r, dt, Double.parseDouble(p)));
        load();
        clean();
    }

    @FXML
    private void delete() {

        ms.delete(ms.findById(index));
        machineList.clear();
        load();
    }

    @FXML
    private void update() {
        Machine m2 = ms.findById(index);
        //MachinList.set(index, m2);
        m2.setDateAchat(dt);
        m2.setMarque(marque.getText());
        m2.setReference(reference.getText());
        m2.setPrix(Double.parseDouble(prix.getText()));
        ms.update(m2);
        machineList.clear();
        load();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        load();
        machines.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                TablePosition pos = (TablePosition) machines.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                Machine item = machines.getItems().get(row);
                marque.setText(item.getMarque());
                reference.setText(item.getReference());
                prix.setText(Double.toString(item.getPrix()));
                index = item.getId();
                //la convertion de la date a LocalDate
                Date date = item.getDateAchat();
                Instant instant = date.toInstant();
                LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                dateAchat.setValue(localDate.MIN);
//                System.out.println(localDate.MIN);
                load();

            }
        });
    }

    public void clean() {
        marque.setText("");
        reference.setText("");
        dateAchat.setValue(LocalDate.MIN);
        prix.setText("");
    }

    public void load() {
        machineList.clear();
        cMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        cReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        cPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        cDateAchat.setCellValueFactory(new PropertyValueFactory<>("dateAchat"));
        for (Machine m : ms.findAll()) {
            machineList.add(new Machine(m.getId(), m.getMarque(), m.getReference(), m.getDateAchat(), m.getPrix()));
        }

        machines.setItems(machineList);
    }
}
