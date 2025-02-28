package ins.marianao.sailing.fxml.services;

import cat.institutmarianao.sailing.ws.model.User;

public class ServiceSaveUser extends ServiceSaveBase<User> {

	public final String REGISTER = "save";

	// Esto varía según si es update que entonces necesitarías el path de /update o
	// /save
	// O por ejemplo el auth es si necesitas estar logueado para hacer update o
	// crear
	// Y POST es para crear y PUT es para updatear
	public ServiceSaveUser(User entity, String[] path, Method method, boolean auth) throws Exception {
		super(entity, User.class, path, method, auth);
	}

}
