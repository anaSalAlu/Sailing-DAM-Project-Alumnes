package ins.marianao.sailing.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class ControllerBookedTripsList extends AbstractControllerPDF {

	@FXML
    private Button btnPDF;

    @FXML
    private ComboBox<?> cmbRole;

    @FXML
    private ComboBox<?> cmbRole1;

    @FXML
    private ComboBox<?> cmbRole11;

    @FXML
    private ComboBox<?> cmbRole12;

    @FXML
    private ComboBox<?> cmbRole121;

    @FXML
    private TableColumn<?, ?> colFullname;

    @FXML
    private TableColumn<?, ?> colFullname1;

    @FXML
    private TableColumn<?, ?> colIndex;

    @FXML
    private TableColumn<?, ?> colPhone;

    @FXML
    private TableColumn<?, ?> colPhone1;

    @FXML
    private TableColumn<?, ?> colPhone11;

    @FXML
    private TableColumn<?, ?> colPhone12;

    @FXML
    private TableColumn<?, ?> colPhone121;

    @FXML
    private TableColumn<?, ?> colPhone13;

    @FXML
    private TableColumn<?, ?> colPhone14;

    @FXML
    private TableColumn<?, ?> colPhone2;

    @FXML
    private TableColumn<?, ?> colRole;

    @FXML
    private TableColumn<?, ?> colUsername;

    @FXML
    private TableColumn<?, ?> colUsername1;

    @FXML
    private TableView<?> usersTable;

    @FXML
    private BorderPane viewBookedTripsList;

    @FXML
    public void generarPDFClick(ActionEvent event) {

    }


	@Override
	protected String htmlContentToPDF() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String documentTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String documentFileName() {
		// TODO Auto-generated method stub
		return null;
	}


}
