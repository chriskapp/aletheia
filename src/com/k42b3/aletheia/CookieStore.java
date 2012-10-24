package com.k42b3.aletheia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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

		store.get(domain).add(cookie);
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
