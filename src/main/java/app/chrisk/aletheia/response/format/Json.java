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

package app.chrisk.aletheia.response.format;

import java.net.URL;
import java.util.Properties;

import app.chrisk.aletheia.protocol.http.Response;
import org.apache.sling.commons.json.JSONObject;

import app.chrisk.aletheia.processor.ProcessPropertiesAbstract;
import app.chrisk.aletheia.processor.ResponseProcessorInterface;

/**
 * Json
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Json implements ResponseProcessorInterface
{
	public String getName()
	{
		return "JSON Formatter";
	}

	public void process(URL url, app.chrisk.aletheia.protocol.Response response, Properties properties) throws Exception
	{
		if(response instanceof Response)
		{
			Response httpResponse = (Response) response;

			// read json
			JSONObject json = new JSONObject(httpResponse.getBody());

			// set content
			httpResponse.setBody(json.toString(4));
		}
	}
	
	public ProcessPropertiesAbstract getProperties()
	{
		return null;
	}
}
