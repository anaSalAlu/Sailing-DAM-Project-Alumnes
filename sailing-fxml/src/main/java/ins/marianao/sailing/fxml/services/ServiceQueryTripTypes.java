package ins.marianao.sailing.fxml.services;

import java.util.LinkedList;
import java.util.List;

import cat.institutmarianao.sailing.ws.model.TripType;
import cat.institutmarianao.sailing.ws.model.TripType.Category;
import ins.marianao.sailing.fxml.manager.ResourceManager;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

public class ServiceQueryTripTypes extends ServiceQueryBase<TripType> {

	public static final String PATH_REST_TRIP_TYPES = "triptypes";

	private Category[] categories;
	private Double priceFrom;
	private Double priceTo;
	private Integer maxPlacesFrom;
	private Integer maxPlacesTo;
	private Integer durationFrom;
	private Integer durationTo;

	public ServiceQueryTripTypes(Category[] categories, Double priceFrom, Double priceTo, Integer maxPlacesFrom,
			Integer maxPlacesTo, Integer durationFrom, Integer durationTo) {
		this.categories = categories;
		this.priceFrom = priceFrom;
		this.priceTo = priceTo;
		this.maxPlacesFrom = maxPlacesFrom;
		this.maxPlacesTo = maxPlacesTo;
		this.durationFrom = durationFrom;
		this.durationTo = durationTo;
	}

	@Override
	protected List<TripType> customCall() throws Exception {
		Client client = ResourceManager.getInstance().getWebClient();

		WebTarget webTarget = client.target(ResourceManager.getInstance().getParam("web.service.host.url"))
				.path(PATH_REST_TRIP_TYPES).path(PATH_QUERY_ALL);

		if (categories != null) {
			for (Category category : categories) {
				webTarget = webTarget.queryParam("category", category.name());
			}
		}

		if (priceFrom != null) {
			webTarget = webTarget.queryParam("priceFrom", priceFrom);
		}

		if (priceTo != null) {
			webTarget = webTarget.queryParam("priceTo", priceTo);
		}

		if (maxPlacesFrom != null) {
			webTarget = webTarget.queryParam("maxPlacesFrom", maxPlacesFrom);
		}

		if (maxPlacesTo != null) {
			webTarget = webTarget.queryParam("maxPlacesTo", maxPlacesTo);
		}

		if (durationFrom != null) {
			webTarget = webTarget.queryParam("durationFrom", durationFrom);
		}

		if (durationTo != null) {
			webTarget = webTarget.queryParam("durationTo", durationTo);
		}

		Invocation.Builder invocationBuilder = ResourceManager.getInstance().getAuthRequestBuilder(webTarget, true);
		List<TripType> tripTypes = new LinkedList<TripType>();
		try {
			Response response = invocationBuilder.get();

			ResourceManager.getInstance().checkResponseErrors(response);

			tripTypes = response.readEntity(new GenericType<List<TripType>>() {
			});

		} catch (ResponseProcessingException e) {
			e.printStackTrace();
			throw new Exception(
					ResourceManager.getInstance().getText("error.service.response.processing") + " " + e.getMessage());
		} catch (ProcessingException e) {
			e.printStackTrace();
			throw new Exception(
					ResourceManager.getInstance().getText("error.service.processing") + " " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return tripTypes;
	}

}
