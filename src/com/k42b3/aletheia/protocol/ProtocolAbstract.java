/**
 * $Id: ProtocolAbstract.java 23 2012-05-27 23:19:57Z k42b3.x@googlemail.com $
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

package com.k42b3.aletheia.protocol;

import java.net.URL;
import java.util.logging.Logger;

/**
 * ProtocolAbstract
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 * @version    $Revision: 23 $
 */
abstract public class ProtocolAbstract implements ProtocolInterface
{
	protected Request request;
	protected Response response;
	protected CallbackInterface callback;

	protected Logger logger = Logger.getLogger("com.k42b3.aletheia");

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
