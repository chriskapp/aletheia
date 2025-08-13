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

package app.chrisk.aletheia.filter.response;

import app.chrisk.aletheia.protocol.http.Response;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import app.chrisk.aletheia.Aletheia;
import app.chrisk.aletheia.filter.ResponseFilterAbstract;

/**
 * Application
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Application extends ResponseFilterAbstract
{
	public void exec(app.chrisk.aletheia.protocol.Response response) throws Exception
	{
		if(response instanceof Response)
		{
			Response httpResponse = (Response) response;

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
