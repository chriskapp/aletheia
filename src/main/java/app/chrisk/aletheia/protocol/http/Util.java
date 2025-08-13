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
import org.apache.http.message.BasicHeader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

/**
 * Util
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Util 
{
	public final static String[] types = {"HTTP/1.0", "HTTP/1.1"};
	public final static String[] methods = {"OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "CONNECT"};

	public static boolean isValidMethod(String method)
	{
		for(int i = 0; i < Util.methods.length; i++)
		{
			if(Util.methods[i].equals(method))
			{
				return true;
			}
		}
		
		return false;
	}

	public static boolean isValidType(String type)
	{
		for(int i = 0; i < Util.types.length; i++)
		{
			if(Util.types[i].equals(type))
			{
				return true;
			}
		}
		
		return false;
	}

	public static LinkedList<Header> parseHeader(String rawHeader, String delimiter)
	{
		LinkedList<Header> headers = new LinkedList<Header>();

		String[] lines = rawHeader.split(delimiter);

		if(lines.length > 0)
		{
			// headers
			for(int i = 0; i < lines.length; i++)
			{
				int pos = lines[i].indexOf(':');

				if(pos != -1)
				{
					String key = lines[i].substring(0, pos).trim();
					String value = lines[i].substring(pos + 1).trim();

					if(!key.isEmpty() && !value.isEmpty())
					{
						headers.add(new BasicHeader(key, value));
					}
				}
			}
		}

		return headers;
	}

	public static String buildHttpQuery(Map<String, String> params)
	{
		String query = "";
		Iterator<Entry<String, String>> it = params.entrySet().iterator();

		while(it.hasNext())
		{
			Entry<String, String> entry = it.next();

			query+= entry.getKey() + "=";

			if(entry.getValue() != null)
			{
				query+= urlEncode(entry.getValue());
			}

			if(it.hasNext())
			{
				query+= "&";	
			}
		}

		return query;
	}

	public static String buildMessage(String statusLine, LinkedList<Header> header, String body, String delimter)
	{
		StringBuilder str = new StringBuilder();

		// status line
		str.append(statusLine);
		str.append(delimter);

		// headers
		for(int i = 0; i < header.size(); i++)
		{
			str.append(header.get(i).getName() + ": " + header.get(i).getValue() + delimter);
		}

		str.append(delimter);

		// body
		if(body != null && !body.isEmpty())
		{
			str.append(body);
		}

		return str.toString();
	}

	public static String urlEncode(String content)
	{
		try
		{
			return URLEncoder.encode(content, "UTF-8");
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public static String appendQuery(String url, String params)
	{
		if(url.indexOf('?') == -1)
		{
			return url + '?' + params;
		}
		else
		{
			return url + '&' + params;
		}
	}

	/**
	 * This method takes an base url and resolves the href to an url
	 * 
	 * @param baseUrl
	 * @param href
	 * @return string
	 * @throws MalformedURLException
	 */
	public static String resolveHref(String baseUrl, String href) throws MalformedURLException
	{
		URL currentUrl = new URL(baseUrl);

		if(href.startsWith("http://") || href.startsWith("https://"))
		{
			// we have an absolute url
			return href;
		}
		else if(href.startsWith("//"))
		{
			return currentUrl.getProtocol() + ":" + href;
		}
		else if(href.startsWith("?"))
		{
			return currentUrl.getProtocol() + "://" + currentUrl.getHost() + currentUrl.getPath() + href;
		}
		else
		{
			// we have an path wich must be resolved to the base url
			String completePath;

			if(href.startsWith("/"))
			{
				completePath = href;
			}
			else
			{
				int pos = currentUrl.getPath().lastIndexOf('/');
				String path;

				if(pos != -1)
				{
					path = currentUrl.getPath().substring(0, pos);
				}
				else
				{
					path = currentUrl.getPath();
				}

				completePath = path + "/" + href;
			}

			// remove dot segments from path
			String path = removeDotSegments(completePath);

			// build url
			String url = currentUrl.getProtocol() + "://" + currentUrl.getHost() + path;

			// add query params
			int sPos, ePos;
			sPos = href.indexOf('?');

			if(sPos != -1)
			{
				String query;
				ePos = href.indexOf('#');

				if(ePos == -1)
				{
					query = href.substring(sPos + 1);
				}
				else
				{
					query = href.substring(sPos + 1, ePos);
				}

				if(!query.isEmpty())
				{
					url+= "?" + query;
				}
			}

			// add fragment
			sPos = href.indexOf('#');

			if(sPos != -1)
			{
				String fragment = href.substring(sPos + 1);
				
				if(!fragment.isEmpty())
				{
					url+= "#" + fragment;
				}
			}

			return url;
		}
	}
	
	public static String removeDotSegments(String relativePath)
	{
		// remove query or fragment part if any
		int pos = relativePath.indexOf('?');

		if(pos != -1)
		{
			relativePath = relativePath.substring(0, pos);
		}

		pos = relativePath.indexOf('#');

		if(pos != -1)
		{
			relativePath = relativePath.substring(0, pos);
		}

		// if the path contains no slash we have nothing to resolve
		if(relativePath.indexOf('/') == -1)
		{
			return relativePath;
		}

		String[] parts = relativePath.split("/");
		Stack<String> path = new Stack<String>();
		String part;

		for(int i = 0; i < parts.length; i++)
		{
			part = parts[i].trim();

			if(part.isEmpty() || part.equals("."))
			{
			}
			else if(part.equals(".."))
			{
				path.pop();
			}
			else
			{
				path.add(part);
			}
		}

		// build absolute url
		String absoluteUrl = "";

		if(path.size() > 0)
		{
			for(int i = 0; i < path.size(); i++)
			{
				if(i > 0 && path.get(i).indexOf('.') != -1 && path.get(i - 1).equals(path.get(i)))
				{
					// if the element before has the same name and it contains
					// an dot we have probably an file name
					continue;
				}

				absoluteUrl+= "/" + path.get(i);
			}
		}
		else
		{
			absoluteUrl = "/";
		}

		// add last slash
		if(relativePath.endsWith("/") && !absoluteUrl.endsWith("/"))
		{
			absoluteUrl = absoluteUrl + "/";
		}

		return absoluteUrl;
	}
}
