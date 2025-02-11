package ins.marianao.sailing.fxml.services;

import java.sql.Date;
import java.util.List;

import cat.institutmarianao.sailing.ws.model.Trip;
import cat.institutmarianao.sailing.ws.model.Trip.Status;
import cat.institutmarianao.sailing.ws.model.TripType.Category;
import cat.institutmarianao.sailing.ws.model.User;
import ins.marianao.sailing.fxml.manager.ResourceManager;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;

public class ServiceQueryTrips extends ServiceQueryBase<Trip> {

	public static final String PATH_REST_TRIPS = "trips";

	private Category[] category;
	private User client;
	private Date dateFrom;
	private Status[] status;
	private Date dateTo;

	public ServiceQueryTrips(Category[] category, User client, Date dateFrom, Status[] status, Date dateTo) {
		this.category = category;
		this.client = client;
		this.dateFrom = dateFrom;
		this.status = status;
		this.dateTo = dateTo;
	}

	@Override
	protected List<Trip> customCall() throws Exception {
		Client client = ResourceManager.getInstance().getWebClient();

		WebTarget webTarget = client.target(ResourceManager.getInstance().getParam("web.service.host.url"))
				.path(PATH_REST_TRIPS).path(PATH_QUERY_ALL);

		// Ahora mirar si las variables no son nulas y entonces ya asignarle el
		// parámetro
		return null;
	}

}
