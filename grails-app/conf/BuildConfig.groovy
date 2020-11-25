grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    //test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    // configure settings for the run-app JVM
    //run: [maxMemory: 2048, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the run-war JVM
    test: false,
	run: false,
	war: [maxMemory: 2048, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 2048, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
		mavenRepo 'http://repo.spring.io/milestone'
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        runtime 'mysql:mysql-connector-java:5.1.31'
		compile 'org.atmosphere:atmosphere-runtime:2.3.3'
		compile 'org.atmosphere:atmosphere-compat-tomcat:2.0.1'

		compile 'joda-time:joda-time:2.5'
		compile 'org.apache.tika:tika-core:1.7'
		compile 'org.apache.tika:tika-parsers:1.7'
		compile 'net.coobird:thumbnailator:0.4.8'

		compile 'com.thehuxley:fail-predictor:1.0.1'

        compile  ('org.jxls:jxls:2.2.5') {
            exclude group: 'ch.qos.logback', module : 'logback-classic'
        }
        compile ('org.jxls:jxls-poi:1.0.5') {
            exclude group: 'ch.qos.logback', module : 'logback-classic'
        }

		test 'org.grails:grails-datastore-test-support:1.0.2-grails-2.4'
		test 'cglib:cglib-nodep:3.1'

    }

    plugins {
        // plugins for the build system only
        build ":tomcat:8.0.21"

        // plugins for the compile step
        compile ":scaffolding:2.1.2"
        compile ':cache:1.1.7'
        compile ":asset-pipeline:1.8.11"

		compile ":spring-security-core:2.0-RC6"
		compile ":spring-security-oauth2-provider:2.0-RC5"
		compile ":rabbitmq-native:3.1.1"
		compile ":mail:1.0.7"
//		compile ":grails-melody:1.57.0"

        // plugins needed at runtime but not for compilation
        runtime ":hibernate4:4.3.5.4" // or ":hibernate:3.6.10.16"
        runtime ":database-migration:1.4.0"
		runtime ":redis:1.6.6"
//		runtime ":elasticsearch:0.0.4.2"
		runtime ":cors:1.1.6"

        // Uncomment these to enable additional asset-pipeline capabilities
        //compile ":sass-asset-pipeline:1.7.4"
        compile ":less-asset-pipeline:1.7.0"
        //compile ":coffee-asset-pipeline:1.7.0"
        //compile ":handlebars-asset-pipeline:1.3.0.3"
		test ":code-coverage:2.0.3-3"
    }

	grails.tomcat.nio = true
}
