package com.k42b3.aletheia.sample;

import java.net.URL;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

import com.k42b3.aletheia.protocol.Request;

public class BasicAuth implements SampleInterface
{
	public String getName()
	{
		return "Basic Auth";
	}

	public void process(URL url, Request request, Properties properties)
	{
		if(request instanceof com.k42b3.aletheia.protocol.http.Request)
		{
			com.k42b3.aletheia.protocol.http.Request httpRequest = (com.k42b3.aletheia.protocol.http.Request) request;

			String auth = properties.getProperty("user") + ":" + properties.getProperty("password");

			httpRequest.setHeader("Authorization", "Basic " + Base64.encodeBase64String(auth.getBytes()));
		}
	}

	public Properties getProperties()
	{
		Properties props = new Properties();
		props.setProperty("user", "");
		props.setProperty("password", "");

		return props;
	}
}
