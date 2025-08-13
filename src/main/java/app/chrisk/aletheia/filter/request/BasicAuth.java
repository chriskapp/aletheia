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

import app.chrisk.aletheia.protocol.http.Request;
import org.apache.commons.codec.binary.Base64;

import app.chrisk.aletheia.filter.RequestFilterAbstract;

/**
 * BasicAuth
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class BasicAuth extends RequestFilterAbstract
{
	public void exec(app.chrisk.aletheia.protocol.Request request)
	{
		if(request instanceof Request)
		{
			Request httpRequest = (Request) request;

			// get config
			String user = getConfig().getProperty("user");
			String pw = getConfig().getProperty("pw");

			if(!httpRequest.hasHeader("Authorization"))
			{
				httpRequest.setHeader("Authorization", "Basic " + this.computeAuth(user + ":" + pw));
			}
		}
	}

	protected String computeAuth(String key)
	{
		return Base64.encodeBase64String(key.getBytes());
	}
}
