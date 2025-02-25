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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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
		Double priceFrom = Double.parseDouble(priceFromText);

		// Price To
		String priceToText = tfPriceTo.getText();
		Double priceTo = Double.parseDouble(priceToText);

		// Places From
		String placesFromText = tfPlacesFrom.getText();
		Integer maxPlacesFrom = Integer.parseInt(placesFromText);

		// Places To
		String placesToText = tfPlacesTo.getText();
		Integer maxPlacesTo = Integer.parseInt(placesToText);

		// Duration From
		String durationFromText = tfDurationFrom.getText();
		Integer durationFrom = Integer.parseInt(durationFromText);

		// Duration To
		String durationToText = tfDuarationTo.getText();
		Integer durationTo = Integer.parseInt(durationToText);

		final ServiceQueryTripTypes queryTripTypes = new ServiceQueryTripTypes(categories, priceFrom, priceTo,
				maxPlacesFrom, maxPlacesTo, durationFrom, durationTo);

		queryTripTypes.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				scrollTrips.setContent(null);
				ObservableList<TripType> tripTypes = FXCollections.observableArrayList(queryTripTypes.getValue());

			}

		});

		queryTripTypes.setOnFailed(
				new OnFailedEventHandler(ResourceManager.getInstance().getText("error.viewTripTypes.web.service")));

		queryTripTypes.start();
	}
}
