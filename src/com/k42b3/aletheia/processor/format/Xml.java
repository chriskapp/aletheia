/**
 * $Id: Xml.java 31 2012-10-03 13:14:24Z k42b3.x@googlemail.com $
 * 
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

package com.k42b3.aletheia.processor.format;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.processor.ProcessorFactory;
import com.k42b3.aletheia.processor.ProcessorInterface;
import com.k42b3.aletheia.protocol.Response;

/**
 * Xml
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 * @version    $Revision: 31 $
 */
public class Xml implements ProcessorInterface
{
	public String getName()
	{
		return "XML Formatter";
	}

	public void process(Response response) throws Exception
	{
		String content = ProcessorFactory.getResponseContent(response);

		if(content != null)
		{
			// read xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(content));

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
			Aletheia.getInstance().getActiveOut().setText(bout.toString());
		}
	}
}
