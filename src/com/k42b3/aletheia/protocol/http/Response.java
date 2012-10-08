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

package com.k42b3.aletheia.protocol.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

/**
 * Response
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Response extends com.k42b3.aletheia.protocol.Response
{
	protected HttpResponse response;

	protected String line;
	protected Map<String, String> header = new HashMap<String, String>();
	protected String body;

	public Response(HttpResponse response, String content) throws Exception
	{
		super(content);

		this.response = response;

		// get response line
		this.setLine(response.getStatusLine().toString());

		// set headers
		HashMap<String, String> h = new HashMap<String, String>();
		Header[] headers = response.getAllHeaders();

		for(int i = 0; i < headers.length; i++)
		{
			h.put(headers[i].getName(), headers[i].getValue());
		}

		this.setHeaders(h);

		// set body
		this.setBody(this.getContent());
	}

	public int getCode()
	{
		return response.getStatusLine().getStatusCode();
	}

	public void setLine(String line)
	{
		this.line = line;
	}

	public String getLine()
	{
		return this.line;
	}

	public void setHeaders(Map<String, String> headers)
	{
		this.header = headers;
	}

	public Map<String, String> getHeaders()
	{
		return this.header;
	}

	public void setHeader(String key, String value)
	{
		this.header.put(key, value);
	}

	public String getHeader(String key)
	{
		return this.header.get(key);
	}

	public void setBody(String body)
	{
		this.body = body;
	}

	public String getBody()
	{
		return this.body;
	}

	public String toString()
	{
		return Util.buildMessage(this.line, this.header, this.body, "\n");
	}
}
