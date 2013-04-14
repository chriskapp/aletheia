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

import java.net.URL;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.protocol.http.Request;

/**
 * Form
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Form implements SampleInterface
{
	public String getName()
	{
		return "Form";
	}

	public void process() throws Exception
	{
		URL url = new URL(Aletheia.getInstance().getActiveUrl().getText());
		Request request = (com.k42b3.aletheia.protocol.http.Request) Aletheia.getInstance().getActiveIn().getRequest();

		request.setLine("POST", url.getPath());
		request.setHeader("Host", url.getHost());
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		request.setBody("foo=bar");

		Aletheia.getInstance().getActiveIn().update();
	}
}
