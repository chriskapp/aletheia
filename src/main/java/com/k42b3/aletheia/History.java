/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more informations visit <http://aletheia.k42b3.com>
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

package com.k42b3.aletheia;

import java.util.Stack;

/**
 * History
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class History
{
	private Stack<String> urls = new Stack<String>();
	private int pointer = 0;
	private int size = 16;

	public History()
	{
	}

	public void add(String url)
	{
		if(!urls.contains(url))
		{
			if(urls.size() == size)
			{
				urls.remove(0);
			}

			urls.add(url);

			pointer = urls.size() - 1;
		}
	}

	public String next()
	{
		return urls.get(++pointer);
	}

	public boolean hasNext()
	{
		return pointer < urls.size() - 1;
	}

	public String previous()
	{
		return urls.get(--pointer);
	}
	
	public boolean hasPrevious()
	{
		return pointer > 0;
	}
}
