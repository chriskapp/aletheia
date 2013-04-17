/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more informations visit <http://code.google.com/p/aletheia>
 * 
 * Copyright (c) 2010-2013 Christoph Kappestein <k42b3.x@gmail.com>
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

package com.k42b3.aletheia.sample;

import java.util.HashMap;

/**
 * SampleFactory
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class SampleFactory
{
	public static HashMap<String, SampleInterface> samples = new HashMap<String, SampleInterface>();

	public static SampleInterface factory(String name) throws Exception
	{
		if(samples.containsKey(name))
		{
			return samples.get(name);
		}

		if(name.equals("basicAuth"))
		{
			samples.put(name, new BasicAuth());
		}
		else if(name.equals("form"))
		{
			samples.put(name, new Form());
		}
		else if(name.equals("oauthRequestToken"))
		{
			samples.put(name, new OauthRequestToken());
		}
		else if(name.equals("pingback"))
		{
			samples.put(name, new Pingback());
		}
		else if(name.equals("upload"))
		{
			samples.put(name, new Upload());
		}
		else
		{
			throw new Exception("Invalid processor");
		}

		return factory(name);
	}
}
