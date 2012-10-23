package com.k42b3.aletheia;


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

	public static Cookie convert(String rawCookie)
	{
		String[] parts = rawCookie.split(";");
		String[] pair = parts[0].split("=", 2);

		if(pair.length > 0)
		{
			String name = pair[0];
			String value = pair.length > 1 ? pair[1] : "";

			// @todo extract expire / domain part

			return new Cookie(name, value);
		}

		return null;
	}
}
