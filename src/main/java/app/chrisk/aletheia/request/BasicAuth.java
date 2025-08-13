package app.chrisk.aletheia.request;

import java.net.URL;
import java.util.Properties;

import app.chrisk.aletheia.protocol.http.Request;
import org.apache.commons.codec.binary.Base64;

import app.chrisk.aletheia.processor.DefaultProcessProperties;
import app.chrisk.aletheia.processor.ProcessPropertiesAbstract;
import app.chrisk.aletheia.processor.RequestProcessorInterface;

public class BasicAuth implements RequestProcessorInterface
{
	public String getName()
	{
		return "Basic Auth";
	}

	public void process(URL url, app.chrisk.aletheia.protocol.Request request, Properties properties)
	{
		if(request instanceof Request)
		{
			Request httpRequest = (Request) request;

			String auth = properties.getProperty("user") + ":" + properties.getProperty("password");

			httpRequest.setHeader("Authorization", "Basic " + Base64.encodeBase64String(auth.getBytes()));
		}
	}

	public ProcessPropertiesAbstract getProperties()
	{
		Properties props = new Properties();
		props.setProperty("user", "");
		props.setProperty("password", "");

		return new DefaultProcessProperties(props);
	}
}
