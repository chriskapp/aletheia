/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for
 * debugging and finding security issues in web applications. For the current
 * version and more information visit <https://github.com/chriskapp/aletheia>
 *
 * Copyright (c) 2010-2013 Christoph Kappestein <k42b3.x@gmail.com>
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

package app.chrisk.aletheia.request;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import app.chrisk.aletheia.protocol.http.Request;
import app.chrisk.aletheia.Aletheia;
import app.chrisk.aletheia.oauth.OAuth;
import app.chrisk.aletheia.processor.DefaultProcessProperties;
import app.chrisk.aletheia.processor.ProcessPropertiesAbstract;
import app.chrisk.aletheia.processor.RequestProcessorInterface;

/**
 * OAuthRequestToken
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class OAuthRequestToken implements RequestProcessorInterface
{
	public String getName()
	{
		return "Oauth Request Token";
	}

	public void process(URL url, app.chrisk.aletheia.protocol.Request request, Properties properties)
	{
		if (request instanceof Request) {
			Request httpRequest = (Request) request;

			try {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("oauth_consumer_key", properties.getProperty("consumer_key"));
				params.put("oauth_signature_method", "HMAC-SHA1");
				params.put("oauth_timestamp", OAuth.getTimestamp());
				params.put("oauth_nonce", OAuth.getNonce());
				params.put("oauth_callback", "oob");
				params.put("oauth_version", OAuth.getVersion());

				String baseString = OAuth.buildBaseString("POST", url.toString(), params);
				String signature = OAuth.getSignature("HMAC-SHA1").build(baseString, properties.getProperty("consumer_secret"), "");

				params.put("oauth_signature", signature);

				httpRequest.setLine("POST", url.getPath());
				httpRequest.setHeader("Authorization", "OAuth realm=\"Aletheia\", " + OAuth.buildAuthString(params));
			} catch(Exception e) {
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
