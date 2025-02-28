package ins.marianao.sailing.fxml;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cat.institutmarianao.sailing.ws.model.Admin;
import cat.institutmarianao.sailing.ws.model.Client;
import cat.institutmarianao.sailing.ws.model.User;
import cat.institutmarianao.sailing.ws.model.User.Role;
import ins.marianao.sailing.fxml.exception.OnFailedEventHandler;
import ins.marianao.sailing.fxml.manager.ResourceManager;
import ins.marianao.sailing.fxml.services.ServiceQueryUsers;
import ins.marianao.sailing.fxml.services.ServiceSaveBase;
import ins.marianao.sailing.fxml.services.ServiceSaveBase.Method;
import ins.marianao.sailing.fxml.services.ServiceSaveUser;
import ins.marianao.sailing.fxml.utils.Formatters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;

public class ControllerFormRegister implements Initializable {

	@FXML
	private Button btnSave;

	@FXML
	private ComboBox<Pair<String, String>> cbRole;

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

		List<Pair<String, String>> roles = Stream.of(User.Role.values())
				.map(new Function<Role, Pair<String, String>>() {
					@Override
					public Pair<String, String> apply(Role t) {
						String key = t.name();
						return new Pair<String, String>(key, resources.getString("text.User." + key));
					}

				}).collect(Collectors.toList());

		ObservableList<Pair<String, String>> listRoles = FXCollections.observableArrayList(roles);
		listRoles.add(0, null);

		this.cbRole.setItems(listRoles);
		this.cbRole.setConverter(Formatters.getStringPairConverter("User"));
	}

	@FXML
	public void registerClick(ActionEvent event) throws Exception {
		String username = tfUser.getText();
		String password = tfPwd.getText();
		String confirm = tfConfirmPwd.getText();
		String name = tfName.getText();
		String phone = tfPhone.getText();

		if (!password.equals(confirm)) {
			System.out.println("No igual");
		}

		ResourceManager manager = ResourceManager.getInstance();

		Method method = ServiceSaveBase.Method.POST;
		String[] path = new String[] { ServiceQueryUsers.PATH_REST_USERS, "save" };

		Admin admin;
		Client client;
		final ServiceSaveUser newUser;
		if (cbRole != null && cbRole.equals(Role.ADMIN)) {
			admin = new Admin();
			admin.setRole(Role.ADMIN);
			admin.setUsername(username);
			admin.setPassword(password);
			newUser = new ServiceSaveUser(admin, path, method, false);
		} else {
			client = new Client();
			client.setRole(Role.CLIENT);
			client.setFullName(name);
			client.setPhone(Integer.parseInt(phone));
			newUser = new ServiceSaveUser(client, path, method, false);
		}

		newUser.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				System.out.println("Usuario registrado");
			}
		});

		newUser.setOnFailed(
				new OnFailedEventHandler(ResourceManager.getInstance().getText("error.viewUsers.delete.web.service")));

		newUser.start();

	}

}
