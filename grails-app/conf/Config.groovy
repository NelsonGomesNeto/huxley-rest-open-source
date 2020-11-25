import grails.plugin.springsecurity.oauthprovider.approval.UserApprovalSupport

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

grails.app.context = '/api'

environments {
    development {
        grails.logging.jul.usebridge = true
		grails.assets.minifyJs = false
		grails.assets.minifyCss = false
//		grails.serverURL = "http://192.168.0.40:8080/api"
    }
    production {
        grails.logging.jul.usebridge = false
        grails.serverURL = "https://www.thehuxley.com/api"
    }
}

// log4j configuration
log4j.main = {
    // Example of changing the log pattern for the default console appender:
    //
	console name: 'stdout', layout: pattern(conversionPattern: '[%-5p] %d %c - %m%n')

	environments {
		development {
			debug stdout : ["grails.app.rabbit.consumers", "grails.app.services.com.thehuxley"] //"org.hibernate.SQL"
			//trace stdout: ["org.hibernate.type"]
		}

		test {
			//debug stdout: ["grails.app.services.com.thehuxley", "org.hibernate.SQL"]
			//trace stdout: ["org.hibernate.type"]
			//trace stdout: ["org.hibernate.SQL"]
		}

		production {
			info    'org.codehaus.groovy.grails.web.servlet',        // controllers
					'org.codehaus.groovy.grails.web.pages',          // GSP
					'org.codehaus.groovy.grails.web.sitemesh',       // layouts
					'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
					'org.codehaus.groovy.grails.web.mapping',        // URL mapping
					'org.codehaus.groovy.grails.commons',            // core / classloading
					'org.codehaus.groovy.grails.plugins',            // plugins
					'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
					'org.springframework',
					'org.hibernate',
					"grails.app.services.com.thehuxley",
					'net.sf.ehcache.hibernate'
		}
	}

}

grails {
	redis {
		timeout = 2000
		port = System.getenv()['REDIS_PORT_6379_TCP_PORT'] ?: 6379
		host = System.getenv()['REDIS_PORT_6379_TCP_ADDR'] ?: 'localhost'
	}
}

//elasticSearch {
//	datastoreImpl = 'hibernateDatastore'
//	bulkIndexOnStartup = true
//	maxBulkRequest = 500
//	searchableProperty.name = 'searchable'
//	client {
//		mode = 'node'
//		host = [host:'localhost', port:9300]
//	}
//}

environments {
	development {
//		elasticSearch {
//			client.mode = 'local'
//			bulkIndexOnStartup = false
//			searchableProperty.name = 'disablesearchable'
//		}
	}

	test {
		elasticSearch {
			client.mode = 'local'
			bulkIndexOnStartup = false
			searchableProperty.name = 'disablesearchable'
		}
	}
}

grails.plugin.springsecurity.password.algorithm = 'SHA-512'
grails.plugin.springsecurity.password.hash.iterations = 1
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.thehuxley.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.thehuxley.UserRole'
grails.plugin.springsecurity.authority.className = 'com.thehuxley.Role'
grails.plugin.springsecurity.useBasicAuth = true
grails.plugin.springsecurity.basic.realmName = "The Huxley"
grails.plugin.springsecurity.basic.credentialsCharset = 'UTF-8'
//grails.plugin.springsecurity.logout.filterProcessesUrl = '/logout'
//grails.plugin.springsecurity.apf.filterProcessesUrl = '/login'
//grails.plugin.springsecurity.apf.usernameParameter = 'username'
//grails.plugin.springsecurity.apf.passwordParameter = 'password'

grails.plugin.springsecurity.filterChain.chainMap = [
		'/oauth/token': (
				'JOINED_FILTERS,' +
						'-oauth2ProviderFilter,' +
						'-securityContextPersistenceFilter,' +
						'-logoutFilter,-authenticationProcessingFilter,' +
						'-rememberMeAuthenticationFilter,' +
						'-exceptionTranslationFilter'
		),

		'/v1/**': (
				'JOINED_FILTERS,' +
						'-securityContextPersistenceFilter,' +
						'-logoutFilter,' +
						'-authenticationProcessingFilter,' +
						'-rememberMeAuthenticationFilter,' +
						'-oauth2BasicAuthenticationFilter,' +
						'-exceptionTranslationFilter'
		),

		'/**': (
				'JOINED_FILTERS,' +
						'-statelessSecurityContextPersistenceFilter,' +
						'-oauth2ProviderFilter,' +
						'-clientCredentialsTokenEndpointFilter,' +
						'-oauth2BasicAuthenticationFilter,' +
						'-oauth2ExceptionTranslationFilter'
		)
]

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        '/': ['permitAll'],
        '/css/**': ['permitAll'],
        '/image/**': ['permitAll'],
        '/js/**': ['permitAll'],
        '/font/**': ['permitAll'],
        '/vendor/**': ['permitAll'],
        '/assets/**': ['permitAll'],
        '/login/**': ['permitAll'],
		'/oauth/authorize.dispatch': ["isFullyAuthenticated() and (request.getMethod().equals('GET') or request.getMethod().equals('POST'))"],
		'/oauth/token.dispatch': ["isFullyAuthenticated() and request.getMethod().equals('POST')"]
]

grails.plugin.springsecurity.providerNames = [
		'clientCredentialsAuthenticationProvider',
		'daoAuthenticationProvider',
		'anonymousAuthenticationProvider',
		'rememberMeAuthenticationProvider'
]

cors.expose.headers = 'total'
cors.headers = ['Access-Control-Allow-Headers': 'Content-Type, Authorization, X-Atmosphere-Framework, X-Atmosphere-Tracking-Id, X-Atmosphere-Transport, X-Atmosphere-Trackmessagesize, X-Heartbeat-Server']

rabbitmq {
	connection = {
		connection host: System.getenv()['RABBIT_PORT_5672_TCP_ADDR'] ?: 'localhost', username: "", password: ""
	}

	queues = {
		queue name: "submission_queue", durable: true
		queue name: "evaluation_queue", durable: true
		queue name: "oracle_queue", durable: true
		queue name: "oracle_result_queue", durable: true
		queue name: "statistics_queue", durable: true
	}
}

grails {
	mail {
		host = "smtp.gmail.com"
		port = 465
		username = "contato@thehuxley.com"
		password = ""
		props = ["mail.smtp.auth":"true",
				 "mail.smtp.socketFactory.port":"465",
				 "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
				 "mail.smtp.socketFactory.fallback":"false"]
	}
}

huxleyFileSystem {
	base = '/home/huxley/data/submissions'
	profile.images.dir = '/home/huxley/data/images/app/profile'
	institution.images.dir = '/home/huxley/data/images/app/institution'
	problem.images.dir = '/home/huxley/data/images/app/problems'
}



// Added by the Spring Security OAuth2 Provider plugin:
grails.plugin.springsecurity.oauthProvider.clientLookup.className = 'com.thehuxley.Client'
grails.plugin.springsecurity.oauthProvider.authorizationCodeLookup.className = 'com.thehuxley.AuthorizationCode'
grails.plugin.springsecurity.oauthProvider.accessTokenLookup.className = 'com.thehuxley.AccessToken'
grails.plugin.springsecurity.oauthProvider.refreshTokenLookup.className = 'com.thehuxley.RefreshToken'



// Added by the Spring Security OAuth2 Provider plugin:
grails.plugin.springsecurity.oauthProvider.approvalLookup.className = 'com.thehuxley.Approval'
grails.plugin.springsecurity.oauthProvider.approval.auto = UserApprovalSupport.APPROVAL_STORE