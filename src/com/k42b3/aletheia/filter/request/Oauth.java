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

import java.util.HashMap;

import com.k42b3.aletheia.filter.RequestFilterAbstract;
import com.k42b3.aletheia.oauth.SignatureInterface;
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
			values.put("oauth_timestamp", com.k42b3.aletheia.oauth.Oauth.getTimestamp());
			values.put("oauth_nonce", com.k42b3.aletheia.oauth.Oauth.getNonce());
			values.put("oauth_version", com.k42b3.aletheia.oauth.Oauth.getVersion());

			// add get vars to values
			values.putAll(httpRequest.getParams());

			// build base string
			String baseString = com.k42b3.aletheia.oauth.Oauth.buildBaseString(httpRequest.getMethod(), request.getUrl().toString(), values);

			// get signature
			SignatureInterface sig = com.k42b3.aletheia.oauth.Oauth.getSignature(method);

			// build signature
			values.put("oauth_signature", sig.build(baseString, consumerSecret, tokenSecret));

			// add header to request
			if(!httpRequest.hasHeader("Authorization"))
			{
				httpRequest.setHeader("Authorization", "OAuth realm=\"Aletheia\", " + com.k42b3.aletheia.oauth.Oauth.buildAuthString(values));
			}
		}
	}


}
