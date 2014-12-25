package com.k42b3.aletheia.request;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.oauth.Oauth;
import com.k42b3.aletheia.processor.DefaultProcessProperties;
import com.k42b3.aletheia.processor.ProcessPropertiesAbstract;
import com.k42b3.aletheia.processor.RequestProcessorInterface;
import com.k42b3.aletheia.protocol.Request;

public class OauthRequestToken implements RequestProcessorInterface
{
	public String getName()
	{
		return "Oauth Request Token";
	}

	public void process(URL url, Request request, Properties properties)
	{
		if(request instanceof com.k42b3.aletheia.protocol.http.Request)
		{
			com.k42b3.aletheia.protocol.http.Request httpRequest = (com.k42b3.aletheia.protocol.http.Request) request;

			try
			{
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("oauth_consumer_key", properties.getProperty("consumer_key"));
				params.put("oauth_signature_method", "HMAC-SHA1");
				params.put("oauth_timestamp", Oauth.getTimestamp());
				params.put("oauth_nonce", Oauth.getNonce());
				params.put("oauth_callback", "oob");
				params.put("oauth_version", Oauth.getVersion());

				String baseString = Oauth.buildBaseString("POST", url.toString(), params);
				String signature = Oauth.getSignature("HMAC-SHA1").build(baseString, properties.getProperty("consumer_secret"), "");

				params.put("oauth_signature", signature);

				httpRequest.setLine("POST", url.getPath());
				httpRequest.setHeader("Authorization", "OAuth realm=\"Aletheia\", " + Oauth.buildAuthString(params));
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
		props.setProperty("consumer_key", "");
		props.setProperty("consumer_secret", "");

		return new DefaultProcessProperties(props);
	}
}
