package ins.marianao.sailing.fxml.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;

import cat.institutmarianao.sailing.ws.model.Admin;
import cat.institutmarianao.sailing.ws.model.User;
import ins.marianao.sailing.fxml.ControllerMenu;
import ins.marianao.sailing.fxml.exception.SessionExpiredException;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

public class ResourceManager {
	/* Report */
	public static final int COL_1 = 15; // Cat.
	public static final int COL_2 = 12; // Date
	public static final int COL_3 = 35; // User
	public static final int COL_4 = 10; // Prio.
	public static final int COL_5 = 12; // Status
	public static final int GAP = 3;
	public static final int W_TOTAL = COL_1 + COL_2 + COL_3 + COL_4 + COL_5 + 4 * GAP;
	public static final String CHR_BORDER = "·";

	public static final String BASE_URI = "src/main/resources/ins/marianao/sailing/fxml/resources/";
	public static final String BASE_DIR = "data";
	public static final String FILE_REPORT_USERS = "users_report.pdf";
	public static final String FILE_REPORT_TRIPS = "trips_report.pdf";
	public static final String FILE_REPORT_TRIPS_LIST = "trips_list_report.pdf";

	private User currentUser; // Logged in user
	private String currentToken; // Token in use

	private static ResourceManager instance = null;
	private Stage stage;
	private Application app;
	private ResourceBundle applicationBundle;
	private ResourceBundle translationsBundle;
	private ControllerMenu menuController;

	protected ResourceManager() {
	}

	public static ResourceManager getInstance() {
		if (instance == null) {
			instance = new ResourceManager();
		}
		return instance;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Application getApp() {
		return app;
	}

	public void setApp(Application app) {
		this.app = app;
	}

	public ResourceBundle getApplicationBundle() {
		return applicationBundle;
	}

	public void setApplicationBundle(ResourceBundle applicationBundle) {
		this.applicationBundle = applicationBundle;
	}

	public ResourceBundle getTranslationBundle() {
		return translationsBundle;
	}

	public void setTranslationBundle(ResourceBundle translationsBundle) {
		this.translationsBundle = translationsBundle;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public String getCurrentToken() {
		return currentToken;
	}

	public void setCurrentToken(String currentToken) {
		this.currentToken = currentToken;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public ControllerMenu getMenuController() {
		return menuController;
	}

	public void setMenuController(ControllerMenu menuController) {
		this.menuController = menuController;
	}

	public boolean isAdmin() {
		return (currentUser != null && currentUser instanceof Admin);
	}

	public String getText(String key) {
		return this.translationsBundle.getString(key);
	}

	public String getParam(String key) {
		return this.applicationBundle.getString(key);
	}

	public void checkResponseErrors(Response response) throws Exception {
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			return;
		}

		if (!response.hasEntity()) {
			throw new Exception(this.getText("error.service.internal.error"));
		}

		String body = response.readEntity(String.class);
		// ENTITY {"timestamp":"09/10/2022 12:47:26","status":500,"errors":["User
		// password do not match"]}
		JsonReader jsonReader = Json.createReader(new StringReader(body));
		JsonObject jsonBody = jsonReader.readObject();

		if (this.isAuthenticated()) {
			// Check Session Expired
			if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
				if (jsonBody.containsKey("message") && jsonBody.get("message").toString().contains("JWT expired")) {
					throw new SessionExpiredException(this.getText("error.session.expired"));
				}
			}
		}

		String responseError = this.getText("error.service.error.code") + " " + response.getStatus();
		if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
			responseError += " " + this.getText("error.service.forbidden");
		}

		if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
			responseError += " " + this.getText("error.service.not.found");
		}

		if (jsonBody.containsKey("message")) {
			responseError += ": " + jsonBody.get("message");
		}
		if (jsonBody.containsKey("errors")) {
			JsonArray jsonErrors = jsonBody.getJsonArray("errors");
			if (jsonErrors != null) {
				List<String> errors = jsonErrors.stream().map(new Function<JsonValue, String>() {
					@Override
					public String apply(JsonValue v) {
						return v.toString();
					}

				}).collect(Collectors.toList());
				responseError += System.lineSeparator() + StringUtils.join(errors.iterator(), System.lineSeparator());
			}
		}
		jsonReader.close();

		throw new Exception(responseError);
	}

	public boolean isAuthenticated() {
		return currentUser != null;
	}

	/**
	 * REST client (jersey) with configurations: JSON (jackson) + SSL suport using
	 * sailingws KeyStore as trustStore
	 * 
	 * @return REST client
	 * @throws Exception
	 */
	public Client getWebClient() throws Exception {
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null, new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

		} }, new java.security.SecureRandom());

		HostnameVerifier allowAll = new HostnameVerifier() {

			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		/*
		 * ClientConfig clientConfig = new ClientConfig();
		 * clientConfig.register(JacksonFeature.class); return
		 * ClientBuilder.newClient(clientConfig);
		 */

		// probe the keystore file and load the keystore entries
		KeyStore trustStore = KeyStore.getInstance(
				new File(getClass().getClassLoader().getResource(this.getParam("client.ssl.trust-store")).toURI()),
				this.getParam("client.ssl.trust-store-password").toCharArray());

		return ClientBuilder.newBuilder().register(ClientObjectMapperProvider.class).register(JacksonFeature.class) // JSON
																													// usually
																													// auto-discovered
																													// JacksonFeature
																													// JacksonJsonProvider
																													// JacksonJaxbJsonProvider
				.register(JacksonJsonProvider.class)
				// .sslContext(sslcontext)
				.hostnameVerifier(allowAll).trustStore(trustStore).build();
	}

	public Invocation.Builder getAuthRequestBuilder(WebTarget webTarget, boolean json) {
		// Auth header
		// authorization: Bearer eyJhbGciOi....

		Invocation.Builder invocationBuilder = json ? webTarget.request(MediaType.APPLICATION_JSON)
				: webTarget.request();

		invocationBuilder = invocationBuilder.header(this.getParam("jwt.response.authorization.header"),
				this.getParam("jwt.response.authorization.value") + " " + this.getCurrentToken());

		System.out.println(this.getParam("jwt.response.authorization.header"));
		System.out.println(this.getParam("jwt.response.authorization.value") + " " + this.getCurrentToken());

		return invocationBuilder;
	}

	public String usersDirectoryHtml(List<User> users, List<Pair<String, String>> filters) {

		String report = "";

		// TODO Html table containing directory user list

		return report;
	}

	public void exportUsers(File file, SortedSet<User> toExport) throws Exception {
		// Serialization
		ObjectOutputStream fileOut = null;
		try {

			fileOut = new ObjectOutputStream(new FileOutputStream(file));

			fileOut.writeObject(toExport);

		} catch (FileNotFoundException e) {

			throw new Exception("File not found: " + e.getMessage());

		} catch (Exception e) {

			throw new Exception("Error: " + e.getMessage());

		} finally {

			if (fileOut != null) {
				fileOut.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public SortedSet<User> importUsers(File file) throws Exception {
		SortedSet<User> imported = new TreeSet<>();

		// Serialization
		ObjectInputStream fileIn = null;
		try {
			fileIn = new ObjectInputStream(new FileInputStream(file));

			imported = (SortedSet<User>) fileIn.readObject();

		} catch (FileNotFoundException e) {

			throw new Exception("File not found: " + e.getMessage());

		} catch (IOException e) {

			throw new Exception("I/O Error: " + e.getMessage());

		} catch (Exception e) {

			throw new Exception("Error: " + e.getMessage());

		} finally {

			if (fileIn != null) {
				fileIn.close();
			}
		}

		return imported;
	}
}
