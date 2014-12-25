package com.k42b3.aletheia.request;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.processor.DefaultProcessProperties;
import com.k42b3.aletheia.processor.ProcessPropertiesAbstract;
import com.k42b3.aletheia.processor.RequestProcessorInterface;
import com.k42b3.aletheia.protocol.Request;
import com.k42b3.aletheia.protocol.http.Util;

public class Oauth2AuthorizationRequest implements RequestProcessorInterface
{
	public String getName()
	{
		return "Oauth2 Authorization Request";
	}

	public void process(URL url, Request request, Properties properties)
	{
		if(request instanceof com.k42b3.aletheia.protocol.http.Request)
		{
			com.k42b3.aletheia.protocol.http.Request httpRequest = (com.k42b3.aletheia.protocol.http.Request) request;

			try
			{
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("response_type", "code");
				params.put("client_id", properties.getProperty("client_id"));
				params.put("redirect_uri", properties.getProperty("redirect_uri"));
				params.put("scope", "");
				params.put("state", "");

				httpRequest.setLine("GET", url.getPath());
				httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httpRequest.setBody(Util.buildHttpQuery(params));
			}
			catch(Exception e)
			{
				Aletheia.handleException(e);
			}
		}
	}

	public ProcessPropertiesAbstract getProperties()
	{
		Properties props = new Properties();
		props.setProperty("url", "");
		props.setProperty("client_id", "");
		props.setProperty("client_secret", "");

		return new DefaultProcessProperties(props);
	}
}
