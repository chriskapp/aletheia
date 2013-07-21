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

package com.k42b3.aletheia.search;

import com.k42b3.aletheia.protocol.Response;


/**
 * ProcessorFactory
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class SearchFactory
{
	/**
	 * Returns the fitting search engine for the response
	 * 
	 * @param response
	 * @return SearchInterface
	 * @throws Exception
	 */
	public static SearchInterface getFittingEngine(Response response) throws Exception
	{
		String name = "Text";

		if(response instanceof com.k42b3.aletheia.protocol.http.Response)
		{
			com.k42b3.aletheia.protocol.http.Response httpResponse = (com.k42b3.aletheia.protocol.http.Response) response;

			if(httpResponse.getHeader("Content-Type").indexOf("text/html") != -1)
			{
				name = "Html";
			}
		}

		return getEngine(name);
	}

	public static SearchInterface getEngine(String name) throws Exception
	{
		String cls = "com.k42b3.aletheia.search.engine." + name;
		Class<SearchInterface> c = (Class<SearchInterface>) Class.forName(cls);

		return c.newInstance();
	}
}
