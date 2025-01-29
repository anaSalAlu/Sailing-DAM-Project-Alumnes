package ins.marianao.sailing.fxml;

import java.net.URL;
import java.util.ResourceBundle;

import ins.marianao.sailing.fxml.manager.ResourceManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class ControllerFormRegister implements Initializable {

	@FXML
	private Button btnSave;

	@FXML
	private ComboBox<?> cbRole;

	@FXML
	private Label lblConfirmPwd;

	@FXML
	private Label lblName;

	@FXML
	private Label lblPhone;

	@FXML
	private Label lblPwd;

	@FXML
	private Label lblTitle;

	@FXML
	private Label lblUser;

	@FXML
	private TextField tfConfirmPwd;

	@FXML
	private TextField tfName;

	@FXML
	private TextField tfPhone;

	@FXML
	private TextField tfPwd;

	@FXML
	private TextField tfUser;

	@FXML
	private BorderPane viewFormRegister;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	/**
	 * Called when btnLogin button is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void registerClick(ActionEvent event) {

		ResourceManager.getInstance().getMenuController().register(this.tfUser.getText(), this.tfPwd.getText(),
				this.tfConfirmPwd.getText(), this.tfName.getText(), this.tfPhone.getText());
	}

}
