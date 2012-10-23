/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more informations visit <http://code.google.com/p/aletheia>
 * 
 * Copyright (c) 2010-2012 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of Aletheia. Aletheia is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * Aletheia is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Aletheia. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.aletheia.filter.request;

import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

import com.k42b3.aletheia.filter.RequestFilterAbstract;
import com.k42b3.aletheia.filter.request.oauth.SignatureInterface;
import com.k42b3.aletheia.filter.request.oauth.Util;
import com.k42b3.aletheia.protocol.Request;

/**
 * Oauth
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Oauth extends RequestFilterAbstract
{
	public void exec(Request request) throws Exception
	{
		if(request instanceof com.k42b3.aletheia.protocol.http.Request)
		{
			com.k42b3.aletheia.protocol.http.Request httpRequest = (com.k42b3.aletheia.protocol.http.Request) request;
			
			// get config
			String consumerKey = getConfig().getProperty("consumer_key");
			String consumerSecret = getConfig().getProperty("consumer_secret");
			String token = getConfig().getProperty("token");
			String tokenSecret = getConfig().getProperty("token_secret");
			String method = getConfig().getProperty("method");

			// add values
			HashMap<String, String> values = new HashMap<String, String>();

			values.put("oauth_consumer_key", consumerKey);
			values.put("oauth_token", token);
			values.put("oauth_signature_method", method);
			values.put("oauth_timestamp", this.getTimestamp());
			values.put("oauth_nonce", this.getNonce());
			values.put("oauth_version", this.getVersion());

			// add get vars to values
			values.putAll(httpRequest.getParams());

			// build base string
			String baseString = this.buildBaseString(httpRequest.getMethod(), request.getUrl().toString(), values);

			// get signature
			SignatureInterface sig;
			String cls = "com.k42b3.aletheia.filter.request.oauth." + this.resolveMethod(method);
			Class c = Class.forName(cls);

			sig = (SignatureInterface) c.newInstance();

			// build signature
			values.put("oauth_signature", sig.build(baseString, consumerSecret, tokenSecret));

			// add header to request
			if(!httpRequest.getHeaders().containsKey("Authorization"))
			{
				httpRequest.setHeader("Authorization", "OAuth realm=\"Aletheia\", " + this.buildAuthString(values));
			}
		}
	}

	protected String buildAuthString(HashMap<String, String> values)
	{
		StringBuilder authString = new StringBuilder();

		Iterator<Entry<String, String>> it = values.entrySet().iterator();

		while(it.hasNext())
		{
			Entry<String, String> e = it.next();

			authString.append(Util.urlEncode(e.getKey()) + "=\"" + Util.urlEncode(e.getValue()) + "\", ");
		}

		String str = authString.toString();


		// remove ", " from string
		str = str.substring(0, str.length() - 2);


		return str;
	}

	protected String buildBaseString(String requestMethod, String url, HashMap<String, String> params)
	{
		StringBuilder base = new StringBuilder();

		base.append(Util.urlEncode(this.getNormalizedMethod(requestMethod)));

		base.append('&');

		base.append(Util.urlEncode(this.getNormalizedUrl(url)));

		base.append('&');

		base.append(Util.urlEncode(this.getNormalizedParameters(params)));

		return base.toString();
	}
	
	protected String getNormalizedParameters(HashMap<String, String> params)
	{
		Iterator<Entry<String, String>> it = params.entrySet().iterator();

		List<String> keys = new ArrayList<String>();

		while(it.hasNext())
		{
			Entry<String, String> e = it.next();

			keys.add(e.getKey());
		}


		// sort params
		Collections.sort(keys);


		// build normalized params
		StringBuilder normalizedParams = new StringBuilder();

		for(int i = 0; i < keys.size(); i++)
		{
			normalizedParams.append(Util.urlEncode(keys.get(i)) + "=" + Util.urlEncode(params.get(keys.get(i))) + "&");
		}

		String str = normalizedParams.toString();


		// remove trailing &
		str = str.substring(0, str.length() - 1);


		return str;
	}

	protected String getNormalizedUrl(String rawUrl)
	{
		try
		{
			rawUrl = rawUrl.toLowerCase();

			URL url = new URL(rawUrl);

			int port = url.getPort();

			if(port == -1 || port == 80 || port == 443)
			{
				return url.getProtocol() + "://" + url.getHost() + url.getPath();
			}
			else
			{
				return url.getProtocol() + "://" + url.getHost() + ":" + port + url.getPath();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();

			return null;
		}
	}

	protected String getNormalizedMethod(String method)
	{
		return method.toUpperCase();
	}

	protected String getTimestamp()
	{
		return "" + (System.currentTimeMillis() / 1000);
	}

	protected String getNonce()
	{
		try
		{
			byte[] nonce = new byte[32];

			Random rand;

			rand = SecureRandom.getInstance("SHA1PRNG");

			rand.nextBytes(nonce);


			return DigestUtils.md5Hex(rand.toString());
		}
		catch(Exception e)
		{
			return DigestUtils.md5Hex("" + System.currentTimeMillis());
		}
	}

	protected String getVersion()
	{
		return "1.0";
	}

	protected String resolveMethod(String method) throws Exception
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("PLAINTEXT", "PLAINTEXT");
		map.put("HMAC-SHA1", "HMACSHA1");
		
		if(map.containsKey(method))
		{
			return map.get(method);
		}
		else
		{
			throw new Exception("Invalid signature method");
		}
	}
}
