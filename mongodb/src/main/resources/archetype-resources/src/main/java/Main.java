#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.io.FileNotFoundException;
import java.io.IOException;

import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.Parameters;
import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.exception.BadRequestException;
import com.strategicgains.restexpress.exception.ConflictException;
import com.strategicgains.restexpress.exception.NotFoundException;
import com.strategicgains.restexpress.pipeline.SimpleConsoleLogMessageObserver;
import com.strategicgains.restexpress.plugin.cache.CacheControlPlugin;
import com.strategicgains.restexpress.plugin.route.RoutesMetadataPlugin;
import ${package}.postprocessor.LastModifiedHeaderPostprocessor;
import ${package}.serialization.ResponseProcessors;
import com.strategicgains.restexpress.util.Environment;
import com.strategicgains.syntaxe.ValidationException;

public class Main
{
	private static final String SERVICE_NAME = "TODO: Enter Service Name";

	public static void main(String[] args) throws Exception
	{
		Configuration config = loadEnvironment(args);
		RestExpress server = new RestExpress()
		    .setName(SERVICE_NAME)
		    .setBaseUrl(config.getBaseUrl())
		    .setDefaultFormat(config.getDefaultFormat())
		    .putResponseProcessor(Format.JSON, ResponseProcessors.json())
		    .putResponseProcessor(Format.XML, ResponseProcessors.xml())
		    .putResponseProcessor(Format.WRAPPED_JSON, ResponseProcessors.wrappedJson())
		    .putResponseProcessor(Format.WRAPPED_XML, ResponseProcessors.wrappedXml())
		    .addPostprocessor(new LastModifiedHeaderPostprocessor())
		    .addMessageObserver(new SimpleConsoleLogMessageObserver());

		Routes.define(config, server);

		new RoutesMetadataPlugin()							// Support basic discoverability.
			.register(server)
			.parameter(Parameters.Cache.MAX_AGE, 86400);	// Cache for 1 day (24 hours).

		new CacheControlPlugin()							// Support caching headers.
			.register(server);

		mapExceptions(server);
		server.bind(config.getPort());
		server.awaitShutdown();
	}

	/**
     * @param server
     */
    private static void mapExceptions(RestExpress server)
    {
    	server
	    	.mapException(ItemNotFoundException.class, NotFoundException.class)
	    	.mapException(DuplicateItemException.class, ConflictException.class)
	    	.mapException(ValidationException.class, BadRequestException.class);
    }

	private static Configuration loadEnvironment(String[] args)
    throws FileNotFoundException, IOException
    {
	    if (args.length > 0)
		{
			return Environment.from(args[0], Configuration.class);
		}

	    return Environment.fromDefault(Configuration.class);
    }
}
