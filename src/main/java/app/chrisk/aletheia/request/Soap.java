/*
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

package app.chrisk.aletheia.request;

import app.chrisk.aletheia.processor.ProcessPropertiesAbstract;
import app.chrisk.aletheia.processor.RequestProcessorInterface;
import app.chrisk.aletheia.protocol.http.Request;

import java.net.URL;
import java.util.Properties;

/**
 * Soap
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Soap implements RequestProcessorInterface
{
	public String getName()
	{
		return "Soap";
	}

	public void process(URL url, app.chrisk.aletheia.protocol.Request request, Properties properties) throws Exception
	{
		if(request instanceof Request)
		{
			Request httpRequest = (Request) request;

			String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			body+= "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n";
			body+= "	<s:Body>\n";
			body+= "	</s:Body>\n";
			body+= "</s:Envelope>\n";

			httpRequest.setLine("POST", url.getPath());
			httpRequest.setHeader("Content-Type", "text/xml");
			httpRequest.setHeader("SOAPAction", "http://localhost.com");
			httpRequest.setBody(body);
		}
	}

	public ProcessPropertiesAbstract getProperties()
	{
		return null;
	}
}
