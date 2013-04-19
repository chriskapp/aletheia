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

package com.k42b3.aletheia.response.format;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import com.k42b3.aletheia.processor.ProcessPropertiesAbstract;
import com.k42b3.aletheia.processor.ResponseProcessorInterface;
import com.k42b3.aletheia.protocol.Response;

/**
 * Xml
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Xml implements ResponseProcessorInterface
{
	public String getName()
	{
		return "XML Formatter";
	}

	public void process(URL url, Response response, Properties properties) throws Exception
	{
		if(response instanceof com.k42b3.aletheia.protocol.http.Response)
		{
			com.k42b3.aletheia.protocol.http.Response httpResponse = (com.k42b3.aletheia.protocol.http.Response) response;
			
			// read xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(httpResponse.getBody()));

			Document doc = db.parse(is);

			// write to string
			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");

			LSSerializer writer = impl.createLSSerializer();
			LSOutput output = impl.createLSOutput();

			output.setByteStream(bout);

			writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
			writer.write(doc, output);

			// set content
			httpResponse.setBody(bout.toString());
		}
	}

	public ProcessPropertiesAbstract getProperties()
	{
		return null;
	}
}
