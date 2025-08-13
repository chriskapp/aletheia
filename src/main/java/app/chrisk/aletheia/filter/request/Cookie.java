/**
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

package app.chrisk.aletheia.filter.request;

import java.util.LinkedList;

import app.chrisk.aletheia.protocol.http.Request;
import app.chrisk.aletheia.CookieStore;
import app.chrisk.aletheia.filter.RequestFilterAbstract;

/**
 * UserAgent
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Cookie extends RequestFilterAbstract
{
	public void exec(app.chrisk.aletheia.protocol.Request request)
	{
		if(request instanceof Request)
		{
			Request httpRequest = (Request) request;

			LinkedList<app.chrisk.aletheia.Cookie> cookies = CookieStore.getInstance().getCookies(httpRequest.getHost());

			if(cookies != null && cookies.size() > 0)
			{
				StringBuilder cookieHeader =  new StringBuilder();

				for(int i = 0; i < cookies.size(); i++)
				{
					cookieHeader.append(cookies.get(i).toString());
					cookieHeader.append("; ");
				}

				if(!httpRequest.hasHeader("Cookie"))
				{
					httpRequest.setHeader("Cookie", cookieHeader.toString());
				}
			}
		}
	}
}
