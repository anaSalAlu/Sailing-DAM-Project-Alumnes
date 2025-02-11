package ins.marianao.sailing.fxml.services;

import java.util.LinkedList;
import java.util.List;

import cat.institutmarianao.sailing.ws.model.User;
import cat.institutmarianao.sailing.ws.model.User.Role;
import ins.marianao.sailing.fxml.manager.ResourceManager;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

public class ServiceQueryUsers extends ServiceQueryBase<User> {

	public static final String PATH_REST_USERS = "users";

	private Role[] roles;
	private String fullName;

	public ServiceQueryUsers(Role[] roles, String fullName) {
		this.roles = roles;
		this.fullName = fullName;
	}

	@Override
	protected List<User> customCall() throws Exception {
		Client client = ResourceManager.getInstance().getWebClient();

		WebTarget webTarget = client.target(ResourceManager.getInstance().getParam("web.service.host.url"))
				.path(PATH_REST_USERS).path(PATH_QUERY_ALL);

		if (this.roles != null) {
			for (Role role : roles) {
				webTarget = webTarget.queryParam("roles", role.name());
			}
		}

		if (this.fullName != null && !this.fullName.isBlank()) {
			webTarget = webTarget.queryParam("fullName", fullName);
		}

		Invocation.Builder invocationBuilder = ResourceManager.getInstance().getAuthRequestBuilder(webTarget, true);

		List<User> users = new LinkedList<User>();
		try {
			Response response = invocationBuilder.get();

			ResourceManager.getInstance().checkResponseErrors(response);

			users = response.readEntity(new GenericType<List<User>>() {
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

		return users;
	}
}
