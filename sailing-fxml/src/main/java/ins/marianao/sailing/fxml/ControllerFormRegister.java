package ins.marianao.sailing.fxml;

import java.net.URL;
import java.util.ResourceBundle;

import cat.institutmarianao.sailing.ws.model.User;
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
		ResourceManager manager = ResourceManager.getInstance();
		User currentUser = manager.getCurrentUser();
		if (!currentUser.isAdmin()) {
			cbRole.setVisible(false);
		}
	}

	@FXML
	public void registerClick(ActionEvent event) {
		String username = tfUser.getText();
		String password = tfPwd.getText();
		String confirm = tfConfirmPwd.getText();
		String name = tfName.getText();
		String phone = tfPhone.getText();
		ResourceManager.getInstance().getMenuController().register(username, password, confirm, name, phone);
	}

}
