package ins.marianao.sailing.fxml.services;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import cat.institutmarianao.sailing.ws.model.Trip;
import cat.institutmarianao.sailing.ws.model.Trip.Status;
import cat.institutmarianao.sailing.ws.model.TripType.Category;
import cat.institutmarianao.sailing.ws.model.User;
import ins.marianao.sailing.fxml.manager.ResourceManager;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

public class ServiceQueryTrips extends ServiceQueryBase<Trip> {

	public static final String PATH_REST_TRIPS = "trips";

	private Category[] categories;
	private User clientUser;
	private Date dateFrom;
	private Status[] statuses;
	private Date dateTo;

	public ServiceQueryTrips(Category[] categories, User clientUser, Date dateFrom, Status[] statuses, Date dateTo) {
		this.categories = categories;
		this.clientUser = clientUser;
		this.dateFrom = dateFrom;
		this.statuses = statuses;
		this.dateTo = dateTo;
	}

	@Override
	protected List<Trip> customCall() throws Exception {
		Client client = ResourceManager.getInstance().getWebClient();

		WebTarget webTarget = client.target(ResourceManager.getInstance().getParam("web.service.host.url"))
				.path(PATH_REST_TRIPS).path(PATH_QUERY_ALL);

		// Ahora mirar si las variables no son nulas y entonces ya asignarle el
		// parámetro
		System.out.println("categories in query: " + categories);
		if (categories != null) {
			for (Category category : categories) {
				System.out.println("category in loop query: " + category);
				webTarget = webTarget.queryParam("category", category.name());
			}
		}

		if (statuses != null) {
			for (Status status : statuses) {
				webTarget = webTarget.queryParam("status", status.name());
			}
		}

		if (clientUser != null) {
			webTarget = webTarget.queryParam("client", clientUser.getUsername());
		}

		if (dateTo != null) {
			webTarget = webTarget.queryParam("to", dateTo);
		}

		if (dateFrom != null) {
			webTarget = webTarget.queryParam("from", dateFrom);
		}

		Invocation.Builder invocationBuilder = ResourceManager.getInstance().getAuthRequestBuilder(webTarget, true);
		List<Trip> trips = new LinkedList<Trip>();
		try {
			Response response = invocationBuilder.get();

			ResourceManager.getInstance().checkResponseErrors(response);

			trips = response.readEntity(new GenericType<List<Trip>>() {
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
		return trips;
	}

}
