/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more information visit <https://github.com/chriskapp/aletheia>
 * 
 * Copyright (c) 2010-2025 Christoph Kappestein <christoph.kappestein@gmail.com>
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

package app.chrisk.aletheia.filter.request;

import java.util.HashMap;

import app.chrisk.aletheia.protocol.http.Request;
import app.chrisk.aletheia.filter.RequestFilterAbstract;
import app.chrisk.aletheia.oauth.SignatureInterface;

/**
 * Oauth
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class OAuth extends RequestFilterAbstract
{
	public void exec(app.chrisk.aletheia.protocol.Request request) throws Exception
	{
		if(request instanceof Request)
		{
			Request httpRequest = (Request) request;

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
			values.put("oauth_timestamp", app.chrisk.aletheia.oauth.OAuth.getTimestamp());
			values.put("oauth_nonce", app.chrisk.aletheia.oauth.OAuth.getNonce());
			values.put("oauth_version", app.chrisk.aletheia.oauth.OAuth.getVersion());

			// add get vars to values
			values.putAll(httpRequest.getParams());

			// build base string
			String baseString = app.chrisk.aletheia.oauth.OAuth.buildBaseString(httpRequest.getMethod(), request.getUrl().toString(), values);

			// get signature
			SignatureInterface sig = app.chrisk.aletheia.oauth.OAuth.getSignature(method);

			// build signature
			values.put("oauth_signature", sig.build(baseString, consumerSecret, tokenSecret));

			// add header to request
			if(!httpRequest.hasHeader("Authorization"))
			{
				httpRequest.setHeader("Authorization", "OAuth realm=\"Aletheia\", " + app.chrisk.aletheia.oauth.OAuth.buildAuthString(values));
			}
		}
	}


}
