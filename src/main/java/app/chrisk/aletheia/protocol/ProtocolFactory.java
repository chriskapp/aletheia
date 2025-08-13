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

import app.chrisk.aletheia.protocol.dns.DNSProtocol;
import app.chrisk.aletheia.protocol.http.HTTPProtocol;
import app.chrisk.aletheia.protocol.https.HTTPSProtocol;
import app.chrisk.aletheia.protocol.whois.WhoisProtocol;

/**
 * ProtocolFactory
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class ProtocolFactory 
{
	public static ProtocolInterface factory(String protocol) throws Exception
	{
		if (protocol.equals("http")) {
			return new HTTPProtocol();
		} else if(protocol.equals("https")) {
			return new HTTPSProtocol();
		} else if(protocol.equals("whois")) {
			return new WhoisProtocol();
		} else if(protocol.equals("dns")) {
			return new DNSProtocol();
		} else {
			throw new Exception("Unknown protocol " + protocol);
		}
	}
}
