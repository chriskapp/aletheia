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

package app.chrisk.aletheia.protocol.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedList;

/**
 * Response
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Response extends app.chrisk.aletheia.protocol.Response
{
	protected HttpResponse response;

	protected String line;
	protected LinkedList<Header> header;
	protected String body;

	public Response(HttpResponse response) throws Exception
	{
		super(response.getEntity() != null ? EntityUtils.toByteArray(response.getEntity()) : null);

		this.response = response;

		// get response line
		this.setLine(response.getStatusLine().toString());

		// set headers
		LinkedList<Header> header = new LinkedList<Header>();
		Header[] headers = response.getAllHeaders();

		for(int i = 0; i < headers.length; i++)
		{
			header.add(headers[i]);
		}

		this.setHeaders(header);

		// read body
		if(this.content != null)
		{
			Charset charset = this.detectCharset();
			String body = "";

			if(charset != null)
			{
				body = new String(this.getContent(), charset);
			}
			else
			{
				if(this.isBinary())
				{
					body = this.toHexdump();
				}
				else
				{
					body = new String(this.getContent(), Charset.forName("UTF-8"));
				}
			}

			this.setBody(body);
		}
		else
		{
			this.setBody("");
		}
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

	public void setHeaders(LinkedList<Header> headers)
	{
		this.header = headers;
	}

	public LinkedList<Header> getHeaders()
	{
		return this.header;
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

	private Charset detectCharset()
	{
		// try to read charset from the header
		String contentType = this.getHeader("Content-Type");

		if(contentType != null)
		{
			try
			{
				Charset charset = ContentType.parse(contentType).getCharset();

				if(charset != null)
				{
					return charset;
				}
			}
			catch(ParseException e)
			{
			}
			catch(UnsupportedCharsetException e)
			{
			}

			// if the content type is text/* use default charset
			if(contentType.indexOf("text/") != -1)
			{
				return Charset.forName("UTF-8");
			}
		}

		// @todo try to parse meta tag therefor we need to convert the content
		// byte[] into an string 
		// <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">

		return null;
	}

	private String toHexdump()
	{
		StringBuilder dump = new StringBuilder();

		for(int i = 0; i < this.content.length; i++)
		{
			String hex = Integer.toHexString(this.content[i]);

			if(hex.length() < 8)
			{
				while(hex.length() < 8)
				{
					hex = "0" + hex;
				}
			}

			if(i > 0 && i % 8 == 0)
			{
				dump.append("\n");
			}

			dump.append(hex);
			dump.append(" ");
		}

		return dump.toString();
	}
	
	/**
	 * Tries to detect whether the content is binary or text content. If the 
	 * content contains more the 8 bytes wich have the upper bytes set it 
	 * returns true
	 * 
	 * @return boolean
	 */
	private boolean isBinary()
	{
		int c = 0;

		for(int i = 0; i < this.content.length; i++)
		{
			if(this.content[i] >> 16 != 0)
			{
				c++;
			}

			if(c > 8)
			{
				return true;
			}
		}

		return false;
	}
}
