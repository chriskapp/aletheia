/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more informations visit <http://code.google.com/p/aletheia>
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

package com.k42b3.aletheia.request;

import java.net.URL;
import java.util.Properties;

import com.k42b3.aletheia.processor.DefaultProcessProperties;
import com.k42b3.aletheia.processor.ProcessPropertiesAbstract;
import com.k42b3.aletheia.processor.RequestProcessorInterface;
import com.k42b3.aletheia.protocol.Request;

/**
 * Form
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Pingback implements RequestProcessorInterface
{
	public String getName()
	{
		return "Pingback";
	}

	public void process(URL url, Request request, Properties properties) throws Exception
	{
		if(request instanceof com.k42b3.aletheia.protocol.http.Request)
		{
			com.k42b3.aletheia.protocol.http.Request httpRequest = (com.k42b3.aletheia.protocol.http.Request) request;

			String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			body+= "<methodCall>\n";
			body+= "	<methodName>pingback.ping</methodName>\n";
			body+= "	<params>\n";
			body+= "		<param>\n";
			body+= "			<value>\n";
			body+= "				<string>" + properties.getProperty("source_uri") + "</string>\n";
			body+= "			</value>\n";
			body+= "		</param>\n";
			body+= "		<param>\n";
			body+= "			<value>\n";
			body+= "				<string>" + properties.getProperty("target_uri") + "</string>\n";
			body+= "			</value>\n";
			body+= "		</param>\n";
			body+= "	</params>\n";
			body+= "</methodCall>\n";

			httpRequest.setLine("POST", url.getPath());
			httpRequest.setHeader("Content-Type", "text/xml");
			httpRequest.setBody(body);
		}
	}

	public ProcessPropertiesAbstract getProperties()
	{
		Properties props = new Properties();
		props.setProperty("source_uri", "");
		props.setProperty("target_uri", "");

		return new DefaultProcessProperties(props);
	}
}
