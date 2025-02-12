package ins.marianao.sailing.fxml;

import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cat.institutmarianao.sailing.ws.model.Trip;
import cat.institutmarianao.sailing.ws.model.Trip.Status;
import cat.institutmarianao.sailing.ws.model.TripType;
import cat.institutmarianao.sailing.ws.model.TripType.Category;
import cat.institutmarianao.sailing.ws.model.User;
import cat.institutmarianao.sailing.ws.model.User.Role;
import ins.marianao.sailing.fxml.exception.OnFailedEventHandler;
import ins.marianao.sailing.fxml.manager.ResourceManager;
import ins.marianao.sailing.fxml.services.ServiceQueryTrips;
import ins.marianao.sailing.fxml.services.ServiceQueryUsers;
import ins.marianao.sailing.fxml.utils.Formatters;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.Pair;

public class ControllerBookedTripsList extends AbstractControllerPDF {

	@FXML
	private ComboBox<Pair<String, String>> cmbCategory;

	@FXML
	private ComboBox<User> cmbClient;

	@FXML
	private DatePicker dateFrom;

	@FXML
	private ComboBox<Pair<String, String>> cmbStatus;

	@FXML
	private DatePicker dateTo;

	@FXML
	private TableColumn<Trip, Number> colBooked;

	@FXML
	private TableColumn<Trip, String> colCancel;

	@FXML
	private TableColumn<Trip, String> colCategory;

	@FXML
	private TableColumn<Trip, String> colComments;

	@FXML
	private TableColumn<Trip, Date> colDate;

	@FXML
	private TableColumn<Trip, String> colDeparture;

	@FXML
	private TableColumn<Trip, String> colDone;

	@FXML
	private TableColumn<Trip, Number> colIndex;

	@FXML
	private TableColumn<Trip, Number> colMax;

	@FXML
	private TableColumn<Trip, Number> colPlaces;

	@FXML
	private TableColumn<Trip, String> colReschedule;

	@FXML
	private TableColumn<Trip, String> colRole;

	@FXML
	private TableColumn<Trip, String> colStatus;

	@FXML
	private TableColumn<Trip, String> colTitle;

	@FXML
	private TableView<Trip> tripsTable;

	@FXML
	private BorderPane viewBookedTripsList;

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		super.initialize(url, resource);

		/*
		 * Control de los combo box
		 */

		// Status
		List<Pair<String, String>> status = Stream.of(Trip.Status.values())
				.map(new Function<Status, Pair<String, String>>() {
					@Override
					public Pair<String, String> apply(Status t) {
						String key = t.name();
						return new Pair<String, String>(key, resource.getString("text.Status." + key));
					}

				}).collect(Collectors.toList());

		ObservableList<Pair<String, String>> listStatus = FXCollections.observableArrayList(status);
		listStatus.add(0, null);

		this.cmbStatus.setItems(listStatus);
		this.cmbStatus.setConverter(Formatters.getStringPairConverter("Status"));

		// Category
		List<Pair<String, String>> category = Stream.of(TripType.Category.values())
				.map(new Function<Category, Pair<String, String>>() {
					@Override
					public Pair<String, String> apply(Category t) {
						String key = t.name();
						return new Pair<String, String>(key, resource.getString("text.Category." + key));
					}

				}).collect(Collectors.toList());

		ObservableList<Pair<String, String>> listCategory = FXCollections.observableArrayList(category);
		listCategory.add(0, null);

		this.cmbCategory.setItems(listCategory);
		this.cmbCategory.setConverter(Formatters.getStringPairConverter("Category"));

		// Cliente
		ServiceQueryUsers queryUsers = new ServiceQueryUsers(new Role[] { Role.CLIENT }, null);

		// Luego aquí cogemos todos los usuarios y los mostramos
		// OJO!!! Hay que mirar si es admin y entonces mostramos el cmb, sino, no
		// El cmb es de tipo User

		// Primero tenemos que hacer un onSucceed() que lo que hace es ejecutar la query
		// y cuando le llega
		// la respuesta, entonces es cuando ejecuta el código que tiene dentro, en este
		// caso el que necesite el getValue()
		// Luego tenemos que hacer el onFailed() y luego podremos hacer el start()
		// para que sirve el start()? Pos buena pregunta

		queryUsers.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				// Aquí creas los elementos que quieres añadir al ComboBox
				List<User> client = queryUsers.getValue();

				// Verificamos que la lista no esté vacía antes de intentar añadirla
				if (client != null && !client.isEmpty()) {
					ObservableList<User> listClient = FXCollections.observableArrayList(client);

					// Añadimos un valor nulo al principio si lo deseas
					listClient.add(0, null);

					// Establecer los elementos del ComboBox
					cmbClient.setItems(listClient);

					// Definir un convertidor para mostrar las claves en formato adecuado
					cmbClient.setConverter(Formatters.getUserConverter());
				} else {
					System.out.println("No se encontraron clientes.");
				}
			}
		});

		queryUsers.setOnFailed(
				new OnFailedEventHandler(ResourceManager.getInstance().getText("error.viewUsers.web.service")));

		queryUsers.start();

		/*
		 * Listeners de los "filtros"
		 */

		this.colIndex.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Trip, Number>, ObservableValue<Number>>() {
					@Override
					public ObservableValue<Number> call(TableColumn.CellDataFeatures<Trip, Number> trip) {
						return new SimpleLongProperty(tripsTable.getItems().indexOf(trip.getValue()) + 1);
					}
				});

		this.cmbCategory.valueProperty().addListener(new ChangeListener<Pair<String, String>>() {
			@Override
			public void changed(ObservableValue<? extends Pair<String, String>> observable,
					Pair<String, String> oldValue, Pair<String, String> newValue) {
				reloadTrips();
			}
		});

		this.cmbStatus.valueProperty().addListener(new ChangeListener<Pair<String, String>>() {
			@Override
			public void changed(ObservableValue<? extends Pair<String, String>> observable,
					Pair<String, String> oldValue, Pair<String, String> newValue) {
				reloadTrips();
			}
		});

		this.cmbClient.valueProperty().addListener(new ChangeListener<User>() {
			@Override
			public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
				reloadTrips();
			}
		});
		this.dateFrom.valueProperty().addListener(new ChangeListener<LocalDate>() {

			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
					LocalDate newValue) {
				reloadTrips();

			}
		});

		this.dateTo.valueProperty().addListener(new ChangeListener<LocalDate>() {

			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
					LocalDate newValue) {
				reloadTrips();

			}
		});

		this.reloadTrips();

		this.tripsTable.setEditable(true);
		this.tripsTable.getSelectionModel().setCellSelectionEnabled(true);

		/*
		 * Control de las columnas
		 */

	}

	private void reloadTrips() {

		// Category
		Category[] categories = null;
		Pair<String, String> category = this.cmbCategory.getValue();
		if (category != null) {
			categories = new Category[] { Category.valueOf(category.getKey()) };
		}

		// Client
		User client = this.cmbClient.getValue();

		// Date From
		LocalDate localDate = this.dateFrom.getValue();
		// TODO Miramos si es nulo y si lo es, entonces pasamos como parámetro el null y
		// sino hacemos la conversión
		Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
		Date dateFrom = (Date) Date.from(instant);

		// Status
		Status[] statuses = null;
		Pair<String, String> status = this.cmbStatus.getValue();
		if (status != null) {
			statuses = new Status[] { Status.valueOf(status.getKey()) };
		}

		// Date To
		LocalDate localDateTo = this.dateTo.getValue();
		Instant instantTo = Instant.from(localDateTo.atStartOfDay(ZoneId.systemDefault()));
		Date dateTo = (Date) Date.from(instantTo);

		final ServiceQueryTrips queryTrips = new ServiceQueryTrips(categories, client, dateFrom, statuses, dateTo);

		queryTrips.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				tripsTable.setEditable(true);

				tripsTable.getItems().clear();

				ObservableList<Trip> trips = FXCollections.observableArrayList(queryTrips.getValue());

				tripsTable.setItems(trips);
			}
		});

		queryTrips.setOnFailed(
				new OnFailedEventHandler(ResourceManager.getInstance().getText("error.viewUsers.web.service")));

		queryTrips.start();
	}

	@Override
	protected String htmlContentToPDF() throws Exception {
		return null;
	}

	@Override
	protected String documentTitle() {
		return ResourceManager.getInstance().getText("fxml.text.viewBookingTripsList.report.title");
	}

	@Override
	protected String documentFileName() {
		return ResourceManager.FILE_REPORT_TRIPS_LIST;
	}

}
