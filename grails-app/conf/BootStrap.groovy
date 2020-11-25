import com.thehuxley.Client

class BootStrap {

    def init = { servletContext ->

		if (!Client.findByClientId("ui")) {
			new Client(
					clientId: "ui",
					clientSecret: Client.NO_CLIENT_SECRET,
					autoApproveScopes: ["true"],
					authorizedGrantTypes: ["password"]
			).save(flush: true)
		}
	}

    def destroy = { }
}
