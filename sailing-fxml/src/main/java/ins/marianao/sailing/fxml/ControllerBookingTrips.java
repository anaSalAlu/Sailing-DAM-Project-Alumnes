package ins.marianao.sailing.fxml;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cat.institutmarianao.sailing.ws.model.TripType;
import cat.institutmarianao.sailing.ws.model.TripType.Category;
import ins.marianao.sailing.fxml.exception.OnFailedEventHandler;
import ins.marianao.sailing.fxml.manager.ResourceManager;
import ins.marianao.sailing.fxml.services.ServiceQueryTripTypes;
import ins.marianao.sailing.fxml.utils.Formatters;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class ControllerBookingTrips implements Initializable {

	@FXML
	private ComboBox<Pair<String, String>> cmbCategory;

	@FXML
	private ScrollPane scrollTrips;

	@FXML
	private TextField tfDuarationTo;

	@FXML
	private TextField tfDurationFrom;

	@FXML
	private TextField tfPlacesFrom;

	@FXML
	private TextField tfPlacesTo;

	@FXML
	private TextField tfPriceFrom;

	@FXML
	private TextField tfPriceTo;

	@FXML
	private BorderPane viewBookingTrips;

	@Override
	public void initialize(URL url, ResourceBundle resource) {

		reloadTripTypes();

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

		this.cmbCategory.valueProperty().addListener(new ChangeListener<Pair<String, String>>() {
			@Override
			public void changed(ObservableValue<? extends Pair<String, String>> observable,
					Pair<String, String> oldValue, Pair<String, String> newValue) {
				reloadTripTypes();
			}
		});

		// Duration To
		this.tfDuarationTo.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				reloadTripTypes();
			}
		});

		// Duration From
		this.tfDurationFrom.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				reloadTripTypes();
			}
		});

		// Places To
		this.tfPlacesTo.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				reloadTripTypes();
			}
		});

		// Places From
		this.tfPlacesFrom.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				reloadTripTypes();
			}
		});

		// Prices To
		this.tfPriceTo.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				reloadTripTypes();
			}
		});

		// Prices From
		this.tfPriceFrom.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				reloadTripTypes();
			}
		});
	}

	private void reloadTripTypes() {
		// Category
		Category[] categories = null;
		Pair<String, String> category = this.cmbCategory.getValue();
		if (category != null) {
			categories = new Category[] { Category.valueOf(category.getKey()) };
		}

		// Price From
		String priceFromText = tfPriceFrom.getText();
		Double priceFrom = null;
		if (priceFromText != null && !priceFromText.isBlank()) {
			priceFrom = Double.parseDouble(priceFromText);
		}

		// Price To
		String priceToText = tfPriceTo.getText();
		Double priceTo = null;
		if (priceFromText != null && !priceFromText.isBlank()) {
			priceTo = Double.parseDouble(priceToText);
		}

		// Places From
		String placesFromText = tfPlacesFrom.getText();
		Integer maxPlacesFrom = null;
		if (placesFromText != null && !placesFromText.isBlank()) {
			maxPlacesFrom = Integer.parseInt(placesFromText);
		}

		// Places To
		String placesToText = tfPlacesTo.getText();
		Integer maxPlacesTo = null;
		if (placesToText != null && !placesToText.isBlank()) {
			maxPlacesTo = Integer.parseInt(placesToText);
		}

		// Duration From
		String durationFromText = tfDurationFrom.getText();
		Integer durationFrom = null;
		if (durationFromText != null && !durationFromText.isBlank()) {
			durationFrom = Integer.parseInt(durationFromText);
		}

		// Duration To
		String durationToText = tfDuarationTo.getText();
		Integer durationTo = null;
		if (durationToText != null && !durationToText.isBlank()) {
			durationTo = Integer.parseInt(durationToText);
		}

		final ServiceQueryTripTypes queryTripTypes = new ServiceQueryTripTypes(categories, priceFrom, priceTo,
				maxPlacesFrom, maxPlacesTo, durationFrom, durationTo);

		queryTripTypes.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			int cards = 4;

			@Override
			public void handle(WorkerStateEvent event) {
				scrollTrips.setContent(null);
				List<TripType> tripTypes = queryTripTypes.getValue();

				VBox container = new VBox();
				container.setFillWidth(true);
				HBox currentHBox = new HBox();

				int cardCount = 0;

				for (TripType tripType : tripTypes) {
					VBox vbox = new VBox();
					// Le damos estilo a la carta
					vbox.setPrefHeight(325);
					vbox.setPrefWidth(220);
					vbox.setStyle(
							"-fx-background-color: #e6e6fa; -fx-padding: 10px; -fx-border-color: black; -fx-border-width: 1px;");
					VBox.setMargin(vbox, new Insets(10, 10, 10, 10));
					Label category = new Label(tripType.getCategory().name());
					category.setStyle("-fx-font-weight: bold;");
					Label title = new Label(tripType.getTitle());
					title.setStyle("-fx-font-weight: bold;");
					Label description = new Label(tripType.getDescription());
					Label maxPlaces = new Label("Max Places: " + tripType.getMaxPlaces());
					Label price = new Label("Price: " + tripType.getPrice());
					Label duration = new Label("Duration: " + tripType.getDuration());
					// Hr
					Label titleDepartures = new Label("DEPARTURES");
					titleDepartures.setStyle("-fx-font-weight: bold;");
					vbox.getChildren().addAll(category, title, description, maxPlaces, price, duration);
					String departures = tripType.getDepartures();
					if (departures != null) {
						Label hours = null;
						if (departures.contains(";")) {
							String[] allDepartures = departures.split(";");
							for (String departure : allDepartures) {
								hours = new Label(departure);
							}
						} else {
							hours = new Label(departures);
						}
						vbox.getChildren().add(hours);
					}

					HBox.setMargin(vbox, new Insets(10, 10, 10, 10));
					currentHBox.getChildren().add(vbox);
					cardCount++;

					if (cardCount % 4 == 0) {
						container.getChildren().add(currentHBox);
						currentHBox = new HBox();
					}
				}

				if (!currentHBox.getChildren().isEmpty()) {
					container.getChildren().add(currentHBox);
				}

				scrollTrips.setContent(container);
			}
		});

		queryTripTypes.setOnFailed(
				new OnFailedEventHandler(ResourceManager.getInstance().getText("error.viewTripTypes.web.service")));

		queryTripTypes.start();
	}
}
