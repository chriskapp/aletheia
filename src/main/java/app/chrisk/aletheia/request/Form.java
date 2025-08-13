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
import java.util.Properties;

import app.chrisk.aletheia.protocol.http.Request;
import app.chrisk.aletheia.processor.ProcessPropertiesAbstract;
import app.chrisk.aletheia.processor.RequestProcessorInterface;

/**
 * Form
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Form implements RequestProcessorInterface
{
	public String getName()
	{
		return "Form";
	}

	public void process(URL url, app.chrisk.aletheia.protocol.Request request, Properties properties) throws Exception
	{
		if(request instanceof Request)
		{
			Request httpRequest = (Request) request;

			httpRequest.setLine("POST", url.getPath());
			httpRequest.setHeader("Host", url.getHost());
			httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpRequest.setBody("foo=bar");
		}
	}

	public ProcessPropertiesAbstract getProperties()
	{
		return null;
	}
}
