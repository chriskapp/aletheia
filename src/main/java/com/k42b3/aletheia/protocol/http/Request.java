/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more informations visit <http://code.google.com/p/aletheia>
 * 
 * Copyright (c) 2010-2015 Christoph Kappestein <k42b3.x@gmail.com>
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

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * Request
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Request extends com.k42b3.aletheia.protocol.Request
{
	protected String host;
	protected String method;
	protected String path;

	protected String line;
	protected LinkedList<Header> header;
	protected String body;

	public Request(URL url, String content)
	{
		super(url, content);
	}

	public void setUrl(URL url)
	{
		super.setUrl(url);

		this.parseUrl(url);
	}

	public void setContent(String content)
	{
		super.setContent(content);

		this.parse(content);
	}

	public String getHost()
	{
		return this.host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getMethod()
	{
		return this.method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public String getPath()
	{
		return this.path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * Returns all GET params from the url as hashmap
	 * 
	 * @return HashMap<String, String>
	 */
	public HashMap<String, String> getParams()
	{
		HashMap<String, String> params = new HashMap<String, String>();
		String query = url.getQuery();
		
		if(query != null)
		{
			String[] parts = query.split("&");

			for(int i = 0; i < parts.length; i++)
			{
				try
				{
					String[] kv = parts[i].split("=", 2);

					if(kv.length == 1)
					{
						params.put(kv[0], "");
					}
					else if(kv.length == 2)
					{
						params.put(kv[0], URLDecoder.decode(kv[1], "UTF-8"));
					}
				}
				catch(UnsupportedEncodingException e)
				{
				}
			}
		}

		return params;
	}

	public String getLine()
	{
		return this.line;
	}

	public void setLine(String line)
	{
		this.line = line;
	}

	public void setLine(String method, String path)
	{
		this.setLine(method + " " + path + " " + HttpProtocol.type);
	}

	public void setHeaders(LinkedList<Header> headers)
	{
		this.header = headers;
	}

	public LinkedList<Header> getHeaders()
	{
		return this.header;
	}

	public void setHeader(Header header, boolean replace)
	{
		if(replace)
		{
			for(int i = 0; i < this.header.size(); i++)
			{
				if(this.header.get(i).getName().toLowerCase().equals(header.getName().toLowerCase()))
				{
					this.header.set(i, header);
					return;
				}
			}
		}

		this.header.add(header);
	}

	public void setHeader(Header header)
	{
		this.setHeader(header, true);
	}

	public void setHeader(String key, String value)
	{
		this.setHeader(new BasicHeader(key, value));
	}

	public String getHeader(String key)
	{
		for(int i = 0; i < this.header.size(); i++)
		{
			if(this.header.get(i).getName().toLowerCase().equals(key.toLowerCase()))
			{
				return this.header.get(i).getValue();
			}
		}

		return null;
	}

	public boolean hasHeader(String key)
	{
		return this.getHeader(key) != null;
	}

	public String getBody()
	{
		return this.body;
	}

	public void setBody(String body)
	{
		this.body = body;
	}

	private void parseUrl(URL url)
	{
		this.host = url.getHost();
		this.path = url.getPath().isEmpty() ? "/" : url.getPath();

		if(url.getQuery() != null)
		{
			this.path+= "?" + url.getQuery();
		}

		if(url.getRef() != null)
		{
			this.path+= "#" + url.getRef();
		}
	}

	private void parse(String request)
	{
		// split header body
		String header = "";
		String body = "";

		int pos = request.indexOf("\n\n");
		if(pos == -1)
		{
			header = request;
			body   = "";
		}
		else
		{
			header = request.substring(0, pos).trim();
			body   = request.substring(pos).trim();
		}

		// get request line
		String rawLine;
		pos = header.indexOf("\n");

		if(pos == -1)
		{
			rawLine = header.trim();
		}
		else
		{
			rawLine = header.substring(0, pos).trim();
			header = header.substring(pos + 1);
		}

		this.setLine(this.parseRequestLine(rawLine));

		// parse header
		this.setHeaders(Util.parseHeader(header, "\n"));
		this.setHeader("Host", this.host);

		// set body
		this.setBody(body);
	}

	private String parseRequestLine(String rawLine)
	{
		// split parts
		String[] parts = rawLine.split(" ");

		String method = "";
		String path = "";
		String type = "";

		if(parts.length >= 1)
		{
			method = parts[0];
			path = this.path;
			type = HttpProtocol.type;
		}
		else
		{
			method = HttpProtocol.method;
			path = this.path;
			type = HttpProtocol.type;
		}

		// check method
		if(!Util.isValidMethod(method))
		{
			method = HttpProtocol.method;
		}

		// check path
		if(path.isEmpty())
		{
			path = "/";
		}

		if(!path.startsWith("/"))
		{
			path = "/" + path;
		}

		// check type
		if(!Util.isValidType(type))
		{
			type = HttpProtocol.type;
		}

		this.method = method;

		return method + " " + path + " " + type;
	}

	public String toString()
	{
		return Util.buildMessage(this.line, this.header, this.body, "\n");
	}
}
