/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more informations visit <http://aletheia.k42b3.com>
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

package com.k42b3.aletheia;

/**
 * Cookie
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
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

	public boolean equals(Object o)
	{
		if(o instanceof Cookie)
		{
			return ((Cookie) o).getName().equals(this.getName());
		}
		else
		{
			return false;
		}
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
