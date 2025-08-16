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

package app.chrisk.aletheia.protocol.dns;

import app.chrisk.aletheia.Aletheia;
import app.chrisk.aletheia.protocol.ProtocolAbstract;
import org.xbill.DNS.Name;
import org.xbill.DNS.Type;
import org.xbill.DNS.lookup.LookupSession;

import java.net.URL;
import java.net.URLStreamHandler;
import java.util.HashMap;

/**
 * DNSProtocol
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class DNSProtocol extends ProtocolAbstract
{
	private final HashMap<Integer, String> types;

    public DNSProtocol()
	{
		// dns types
		types = new HashMap<>();

		types.put(Type.A, "A (IPv4 address)");
		types.put(Type.A6, "A6 (IPv6 address (experimental))");
		types.put(Type.AAAA, "AAAA (IPv6 address)");
		types.put(Type.CNAME, "CNAME (Canonical name)");
		types.put(Type.GPOS, "GPOS (Geographical position)");
		types.put(Type.HINFO, "HINFO (Host information)");
		types.put(Type.LOC, "LOC (Location)");
		types.put(Type.MB, "MB (Mailbox domain name)");
		types.put(Type.MD, "MD (Mail destination)");
		types.put(Type.MF, "MF (Mail forwarder)");
		types.put(Type.MG, "MG (Mail group member)");
		types.put(Type.MINFO, "MINFO (Mailbox information)");
		types.put(Type.MR, "MR (Mail rename name)");
		types.put(Type.MX, "MX (Mail routing information)");
		types.put(Type.NS, "NS (Name server)");
		types.put(Type.NULL, "NULL (Null record)");
		types.put(Type.PTR, "PTR (Domain name pointer)");
		types.put(Type.RP, "RP (Responsible person)");
		types.put(Type.RT, "RT (Router)");
		types.put(Type.SOA, "SOA (Start of authority)");
		types.put(Type.TXT, "TXT (Text strings)");
		types.put(Type.WKS, "WKS (Well known services)");
	}

	public void run() 
	{
		try {
            LookupSession session = LookupSession.defaultBuilder().build();
            StringBuilder out = new StringBuilder();

            for (int key : types.keySet()) {
                var result = session
                    .lookupAsync(Name.fromString(request.getUrl().getHost()), key)
                    .toCompletableFuture()
                    .get();

                out.append("> ").append(types.get(key)).append("\n");

                for (var record : result.getRecords()) {
                    out.append(record).append("\n");
                }
            }

            Aletheia.getInstance().getActiveOut().append(out.toString());

			// create response
			this.response = new Response(Aletheia.getInstance().getActiveOut().getText());

			// call callback
            callback.onResponse(this.request, this.response);
		} catch (Exception e) {
			Aletheia.handleException(e);
		}
	}

	public Request buildRequest(URL url, String content) throws Exception
	{
		return new Request(url, content);
	}
	
	public Request getRequest()
	{
		return (Request) this.request;
	}

	public Response getResponse()
	{
		return (Response) this.response;
	}
	
	public URLStreamHandler getStreamHandler()
	{
		return new DNSURLStreamHandler();
	}
}
