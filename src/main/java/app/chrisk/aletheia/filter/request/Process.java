/*
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

package app.chrisk.aletheia.filter.request;

import app.chrisk.aletheia.filter.RequestFilterAbstract;
import app.chrisk.aletheia.protocol.Request;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Process
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Process extends RequestFilterAbstract
{
    public void exec(Request request)
	{
		String cmd = getConfig().getProperty("cmd");

		try {
			logger.info("Execute: " + cmd);

			CommandLine commandLine = CommandLine.parse(cmd);
            long timeout = 8000;
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
			DefaultExecutor executor = new DefaultExecutor();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteArrayOutputStream baosErr = new ByteArrayOutputStream();
            ByteArrayInputStream bais = new ByteArrayInputStream(request.getContent().getBytes());

			executor.setStreamHandler(new PumpStreamHandler(baos, baosErr, bais));
			executor.setWatchdog(watchdog);
			executor.execute(commandLine);

			logger.info("Output: " + baos);

			request.setContent(baos.toString());
		} catch(Exception e) {
			logger.warning(e.getMessage());
		}
	}
}
