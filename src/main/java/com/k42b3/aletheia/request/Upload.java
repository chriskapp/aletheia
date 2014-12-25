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

import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Properties;

import com.k42b3.aletheia.processor.ProcessPropertiesAbstract;
import com.k42b3.aletheia.processor.RequestProcessorInterface;
import com.k42b3.aletheia.protocol.Request;

/**
 * Upload
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Upload implements RequestProcessorInterface
{
	public String getName()
	{
		return "Upload";
	}

	public void process(URL url, Request request, Properties properties) throws Exception
	{
		if(request instanceof com.k42b3.aletheia.protocol.http.Request)
		{
			com.k42b3.aletheia.protocol.http.Request httpRequest = (com.k42b3.aletheia.protocol.http.Request) request;

			SecureRandom random = new SecureRandom();
			String boundary = "----" + new BigInteger(160, random).toString(32);
			String body = "";
			body+= "--" + boundary + "\n";
			body+= "Content-Disposition: form-data; name=\"userfile\"; filename=\"foo.txt\"" + "\n";
			body+= "Content-Type: text/plain" + "\n";
			body+= "\n";
			body+= "foobar\n";
			body+= "--" + boundary + "--" + "\n";

			httpRequest.setLine("POST", url.getPath());
			httpRequest.setHeader("Host", url.getHost());
			httpRequest.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
			httpRequest.setBody(body);
		}
	}

	public ProcessPropertiesAbstract getProperties()
	{
		return null;
	}
}
