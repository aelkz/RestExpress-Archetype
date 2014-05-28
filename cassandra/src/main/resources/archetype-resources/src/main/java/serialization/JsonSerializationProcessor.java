#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.serialization;

import java.util.UUID;

import org.restexpress.ContentType;
import org.restexpress.serialization.json.JacksonJsonProcessor;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.strategicgains.hyperexpress.domain.hal.HalResource;
import com.strategicgains.hyperexpress.serialization.jackson.HalResourceDeserializer;
import com.strategicgains.hyperexpress.serialization.jackson.HalResourceSerializer;

public class JsonSerializationProcessor
extends JacksonJsonProcessor
{
	public JsonSerializationProcessor()
	{
		super();
		addSupportedMediaTypes(ContentType.HAL_JSON);
	}

	@Override
    protected void initializeModule(SimpleModule module)
    {
	    super.initializeModule(module);
	    module.addDeserializer(UUID.class, new UuidDeserializer());
	    module.addSerializer(UUID.class, new UuidSerializer());

		// Support HalResource (de)serialization.
		module.addDeserializer(HalResource.class, new HalResourceDeserializer());
		module.addSerializer(HalResource.class, new HalResourceSerializer());
    }
}
