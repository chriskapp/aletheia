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

import app.chrisk.aletheia.protocol.http.Request;
import app.chrisk.aletheia.Aletheia;
import app.chrisk.aletheia.processor.DefaultProcessProperties;
import app.chrisk.aletheia.processor.ProcessPropertiesAbstract;
import app.chrisk.aletheia.processor.RequestProcessorInterface;
import app.chrisk.aletheia.protocol.http.Util;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

/**
 * OAuth2AuthorizationRequest
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class OAuth2AuthorizationRequest implements RequestProcessorInterface
{
	public String getName()
	{
		return "Oauth2 Authorization Request";
	}

	public void process(URL url, app.chrisk.aletheia.protocol.Request request, Properties properties)
	{
		if (request instanceof Request) {
			Request httpRequest = (Request) request;

			try {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("response_type", "code");
				params.put("client_id", properties.getProperty("client_id"));
				params.put("redirect_uri", properties.getProperty("redirect_uri"));
				params.put("scope", "");
				params.put("state", "");

				httpRequest.setLine("GET", url.getPath());
				httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httpRequest.setBody(Util.buildHttpQuery(params));
			} catch(Exception e) {
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
