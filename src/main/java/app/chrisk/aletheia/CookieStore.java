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

package app.chrisk.aletheia;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * CookieStore
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class CookieStore
{
	private static CookieStore instance;

	private HashMap<String, LinkedList<Cookie>> store;

	private CookieStore()
	{
		store = new HashMap<String, LinkedList<Cookie>>();
	}

	public void addCookie(String domain, Cookie cookie)
	{
		if(!store.containsKey(domain))
		{
			store.put(domain, new LinkedList<Cookie>());
		}

		LinkedList<Cookie> cookies = store.get(domain);

		if(cookies.indexOf(cookie) != -1)
		{
			cookies.set(cookies.indexOf(cookie), cookie);
		}
		else
		{
			cookies.add(cookie);
		}
	}

	public void setCookies(String domain, LinkedList<Cookie> cookies)
	{
		store.put(domain, cookies);
	}

	public void deleteCookie(String domain, Cookie cookie)
	{
		if(store.containsKey(domain))
		{
			store.get(domain).remove(cookie);
		}
	}

	public void deleteCookies(String domain)
	{
		if(store.containsKey(domain))
		{
			store.get(domain).clear();
		}
	}

	public LinkedList<Cookie> getCookies(String domain)
	{
		if(store.containsKey(domain))
		{
			return store.get(domain);
		}
		else
		{
			return null;
		}
	}

	public static CookieStore getInstance()
	{
		if(instance == null)
		{
			instance = new CookieStore();
		}

		return instance;
	}
}
