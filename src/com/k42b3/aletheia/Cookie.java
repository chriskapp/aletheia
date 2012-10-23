package com.k42b3.aletheia;

import java.util.ArrayList;

public class Cookie
{
	private String name;
	private String value;

	public Cookie(String name, String value)
	{
		this.name = name;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
	
	public String toString()
	{
		return this.name + "=" + this.value;
	}
	
	public static ArrayList<Cookie> convert(String rawCookie)
	{
		return null;
		//return new Cookie();
	}
}
