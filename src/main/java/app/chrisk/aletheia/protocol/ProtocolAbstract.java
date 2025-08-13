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

package app.chrisk.aletheia.protocol;

import java.net.URL;
import java.util.logging.Logger;

/**
 * ProtocolAbstract
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
abstract public class ProtocolAbstract implements ProtocolInterface
{
	protected Request request;
	protected Response response;
	protected CallbackInterface callback;

	protected Logger logger = Logger.getLogger("app.chrisk.aletheia");

	public void setRequest(Request request, CallbackInterface callback) throws Exception
	{
		this.request = request;
		this.callback = callback;
	}

	public Request buildRequest(URL url, String content) throws Exception
	{
		return new Request(url, content);
	}

	public Request getRequest()
	{
		return this.request;
	}

	public Response getResponse()
	{
		return this.response;
	}
	
	public CallbackInterface getCallback()
	{
		return this.callback;
	}
}
