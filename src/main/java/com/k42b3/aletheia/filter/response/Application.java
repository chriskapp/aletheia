/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more informations visit <http://code.google.com/p/aletheia>
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

package com.k42b3.aletheia.filter.response;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.filter.ResponseFilterAbstract;
import com.k42b3.aletheia.protocol.Response;

/**
 * Application
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Application extends ResponseFilterAbstract
{
	public void exec(Response response) throws Exception
	{
		if(response instanceof com.k42b3.aletheia.protocol.http.Response)
		{
			com.k42b3.aletheia.protocol.http.Response httpResponse = (com.k42b3.aletheia.protocol.http.Response) response;

			String contentType = httpResponse.getHeader("Content-Type");

			if(Aletheia.getInstance().getConfig().getApplications().containsKey(contentType))
			{
				String path = Aletheia.getInstance().getConfig().getApplications().get(contentType);
				String url = Aletheia.getInstance().getActiveUrl().getText();
				String cmd = path.replace("${url}", url);

				try
				{
					logger.info("Call " + cmd);

					CommandLine commandLine = CommandLine.parse(cmd);
					DefaultExecutor executor = new DefaultExecutor();

					executor.execute(commandLine);
				}
				catch(Exception e)
				{
					Aletheia.handleException(e);
				}
			}
		}
	}
}
