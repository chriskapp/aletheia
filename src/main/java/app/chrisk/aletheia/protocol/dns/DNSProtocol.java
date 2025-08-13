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
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;

import java.net.URL;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * DNSProtocol
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class DNSProtocol extends ProtocolAbstract
{
	private final HashMap<Integer, String> types;
	private final ExecutorService service;

	public DNSProtocol()
	{
		// dns types
		types = new HashMap<Integer, String>();

		types.put(org.xbill.DNS.Type.A, "A (IPv4 address)");
		types.put(org.xbill.DNS.Type.A6, "A6 (IPv6 address (experimental))");
		types.put(org.xbill.DNS.Type.AAAA, "AAAA (IPv6 address)");
		types.put(org.xbill.DNS.Type.CNAME, "CNAME (Canonical name)");
		types.put(org.xbill.DNS.Type.GPOS, "GPOS (Geographical position)");
		types.put(org.xbill.DNS.Type.HINFO, "HINFO (Host information)");
		types.put(org.xbill.DNS.Type.LOC, "LOC (Location)");
		types.put(org.xbill.DNS.Type.MB, "MB (Mailbox domain name)");
		types.put(org.xbill.DNS.Type.MD, "MD (Mail destination)");
		types.put(org.xbill.DNS.Type.MF, "MF (Mail forwarder)");
		types.put(org.xbill.DNS.Type.MG, "MG (Mail group member)");
		types.put(org.xbill.DNS.Type.MINFO, "MINFO (Mailbox information)");
		types.put(org.xbill.DNS.Type.MR, "MR (Mail rename name)");
		types.put(org.xbill.DNS.Type.MX, "MX (Mail routing information)");
		types.put(org.xbill.DNS.Type.NS, "NS (Name server)");
		types.put(org.xbill.DNS.Type.NULL, "NULL (Null record)");
		types.put(org.xbill.DNS.Type.PTR, "PTR (Domain name pointer)");
		types.put(org.xbill.DNS.Type.RP, "RP (Responsible person)");
		types.put(org.xbill.DNS.Type.RT, "RT (Router)");
		types.put(org.xbill.DNS.Type.SOA, "SOA (Start of authority)");
		types.put(org.xbill.DNS.Type.TXT, "TXT (Text strings)");
		types.put(org.xbill.DNS.Type.WKS, "WKS (Well known services)");

		service = Executors.newSingleThreadExecutor();
	}

	public void run() 
	{
		try {
            for (int key : types.keySet()) {
                service.submit(new RequestWorker(request.getUrl().getHost(), key, types.get(key)));
            }

			service.shutdown();

			Aletheia.getInstance().getActiveOut().append("Requesting DNS records ...");

			service.awaitTermination(10000, TimeUnit.MILLISECONDS);

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
	
	private static class RequestWorker implements Runnable
	{
		private final String host;
		private final int type;
		private final String desc;

		public RequestWorker(String host, int type, String desc)
		{
			this.host = host;
			this.type = type;
			this.desc = desc;
		}

		public void run()
		{
			StringBuilder result = new StringBuilder();

			result.append("> ").append(desc).append("\n");

			try {
				Lookup lookup = new Lookup(host, type);
				lookup.setResolver(new SimpleResolver("8.8.8.8"));

				Record [] records = lookup.run();

				if (records != null && records.length > 0) {
                    for (Record record : records) {
                        result.append(record).append("\n");
                    }
				} else {
					return;
				}

				result.append("\n");
			} catch(Exception e) {
				Aletheia.handleException(e);
			}

			Aletheia.getInstance().getActiveOut().append(result.toString());
		}
	}
}
