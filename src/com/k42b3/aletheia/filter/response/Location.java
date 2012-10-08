/**
 * $Id: Location.java 21 2012-05-27 17:37:22Z k42b3.x@googlemail.com $
 * 
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

package com.k42b3.aletheia.filter.response;

import java.util.Properties;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.filter.ResponseFilterAbstract;
import com.k42b3.aletheia.protocol.Response;
import com.k42b3.aletheia.protocol.http.Util;

/**
 * Charset
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 * @version    $Revision: 21 $
 */
public class Location extends ResponseFilterAbstract
{
	private Properties config = new Properties();

	public void exec(Response response) throws Exception
	{
		if(response instanceof com.k42b3.aletheia.protocol.http.Response)
		{
			com.k42b3.aletheia.protocol.http.Response httpResponse = (com.k42b3.aletheia.protocol.http.Response) response;
			
			if(httpResponse.getCode() >= 300 && httpResponse.getCode() < 400)
			{
				String location = httpResponse.getHeader("Location");

				if(location != null && !Aletheia.getInstance().getActiveUrl().getText().equals(location))
				{
					location = Util.resolveHref(Aletheia.getInstance().getActiveUrl().getText(), location);

					Aletheia.getInstance().run(location);
				}
			}
		}
	}
}
