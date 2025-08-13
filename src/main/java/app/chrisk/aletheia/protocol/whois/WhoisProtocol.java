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

package app.chrisk.aletheia.protocol.whois;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.HashMap;

import org.apache.commons.net.whois.WhoisClient;

import app.chrisk.aletheia.Aletheia;
import app.chrisk.aletheia.protocol.ProtocolAbstract;

/**
 * WhoisProtocol
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class WhoisProtocol extends ProtocolAbstract
{
	protected WhoisClient whois;
	protected HashMap<String, String> list = new HashMap<String, String>();

	protected String stringRequest = "";
	protected String stringResponse = "";
	
	public WhoisProtocol()
	{
		whois = new WhoisClient();

		// whois server list from
		// http://www.nirsoft.net/whois-servers.txt
		list.put("ac", "whois.nic.ac");
		list.put("ae", "whois.aeda.net.ae");
		list.put("aero", "whois.aero");
		list.put("af", "whois.nic.af");
		list.put("ag", "whois.nic.ag");
		list.put("al", "whois.ripe.net");
		list.put("am", "whois.amnic.net");
		list.put("as", "whois.nic.as");
		list.put("asia", "whois.nic.asia");
		list.put("at", "whois.nic.at");
		list.put("au", "whois.aunic.net");
		list.put("ax", "whois.ax ");
		list.put("az", "whois.ripe.net");
		list.put("ba", "whois.ripe.net");
		list.put("be", "whois.dns.be");
		list.put("bg", "whois.register.bg");
		list.put("bi", "whois.nic.bi");
		list.put("biz", "whois.neulevel.biz");
		list.put("bj", "www.nic.bj");
		list.put("br", "whois.nic.br");
		list.put("br.com", "whois.centralnic.com");
		list.put("bt", "whois.netnames.net");
		list.put("by", "whois.cctld.by");
		list.put("bz", "whois.belizenic.bz");
		list.put("ca", "whois.cira.ca");
		list.put("cat", "whois.cat");
		list.put("cc", "whois.nic.cc");
		list.put("cd", "whois.nic.cd");
		list.put("ch", "whois.nic.ch ");
		list.put("ck", "whois.nic.ck");
		list.put("cl", "nic.cl");
		list.put("cn", "whois.cnnic.net.cn");
		list.put("cn.com", "whois.centralnic.com");
		list.put("co", "whois.nic.co");
		list.put("co.nl", "whois.co.nl");
		list.put("com", "whois.verisign-grs.com");
		list.put("coop", "whois.nic.coop");
		list.put("cx", "whois.nic.cx");
		list.put("cy", "whois.ripe.net");
		list.put("cz", "whois.nic.cz");
		list.put("de", "whois.denic.de");
		list.put("dk", "whois.dk-hostmaster.dk");
		list.put("dm", "whois.nic.cx");
		list.put("dz", "whois.nic.dz");
		list.put("edu", "whois.educause.net");
		list.put("ee", "whois.tld.ee");
		list.put("eg", "whois.ripe.net");
		list.put("es", "whois.nic.es");
		list.put("eu", "whois.eu");
		list.put("eu.com", "whois.centralnic.com");
		list.put("fi", "whois.ficora.fi");
		list.put("fo", "whois.nic.fo");
		list.put("fr", "whois.nic.fr");
		list.put("gb", "whois.ripe.net");
		list.put("gb.com", "whois.centralnic.com");
		list.put("gb.net", "whois.centralnic.com");
		list.put("qc.com", "whois.centralnic.com");
		list.put("ge", "whois.ripe.net");
		list.put("gl", "whois.nic.gl");
		list.put("gm", "whois.ripe.net");
		list.put("gov", "whois.nic.gov");
		list.put("gr", "whois.ripe.net");
		list.put("gs", "whois.nic.gs");
		list.put("hk", "whois.hknic.net.hk");
		list.put("hm", "whois.registry.hm");
		list.put("hn", "whois2.afilias-grs.net");
		list.put("hr", "whois.dns.hr");
		list.put("hu", "whois.nic.hu");
		list.put("hu.com", "whois.centralnic.com");
		list.put("ie", "whois.domainregistry.ie");
		list.put("il", "whois.isoc.org.il");
		list.put("in", "whois.inregistry.net");
		list.put("info", "whois.afilias.info");
		list.put("int", "whois.isi.edu");
		list.put("io", "whois.nic.io");
		list.put("iq", "vrx.net");
		list.put("ir", "whois.nic.ir");
		list.put("is", "whois.isnic.is");
		list.put("it", "whois.nic.it");
		list.put("je", "whois.je");
		list.put("jobs", "jobswhois.verisign-grs.com");
		list.put("jp", "whois.jprs.jp");
		list.put("ke", "whois.kenic.or.ke");
		list.put("kg", "whois.domain.kg");
		list.put("kr", "whois.nic.or.kr");
		list.put("la", "whois2.afilias-grs.net");
		list.put("li", "whois.nic.li");
		list.put("lt", "whois.domreg.lt");
		list.put("lu", "whois.restena.lu");
		list.put("lv", "whois.nic.lv");
		list.put("ly", "whois.lydomains.com");
		list.put("ma", "whois.iam.net.ma");
		list.put("mc", "whois.ripe.net");
		list.put("md", "whois.nic.md");
		list.put("me", "whois.nic.me");
		list.put("mil", "whois.nic.mil");
		list.put("mk", "whois.ripe.net");
		list.put("mobi", "whois.dotmobiregistry.net");
		list.put("ms", "whois.nic.ms");
		list.put("mt", "whois.ripe.net");
		list.put("mu", "whois.nic.mu");
		list.put("mx", "whois.nic.mx");
		list.put("my", "whois.mynic.net.my");
		list.put("name", "whois.nic.name");
		list.put("net", "whois.verisign-grs.com");
		list.put("nf", "whois.nic.cx");
		list.put("ng", "whois.nic.net.ng");
		list.put("nl", "whois.domain-registry.nl");
		list.put("no", "whois.norid.no");
		list.put("no.com", "whois.centralnic.com");
		list.put("nu", "whois.nic.nu");
		list.put("nz", "whois.srs.net.nz");
		list.put("org", "whois.pir.org");
		list.put("pl", "whois.dns.pl");
		list.put("pr", "whois.nic.pr");
		list.put("pro", "whois.registrypro.pro");
		list.put("pt", "whois.dns.pt");
		list.put("pw", "whois.nic.pw");
		list.put("ro", "whois.rotld.ro");
		list.put("ru", "whois.tcinet.ru");
		list.put("sa", "saudinic.net.sa");
		list.put("sa.com", "whois.centralnic.com");
		list.put("sb", "whois.nic.net.sb");
		list.put("sc", "whois2.afilias-grs.net");
		list.put("se", "whois.nic-se.se");
		list.put("se.com", "whois.centralnic.com");
		list.put("se.net", "whois.centralnic.com");
		list.put("sg", "whois.nic.net.sg");
		list.put("sh", "whois.nic.sh");
		list.put("si", "whois.arnes.si");
		list.put("sk", "whois.sk-nic.sk");
		list.put("sm", "whois.nic.sm");
		list.put("st", "whois.nic.st");
		list.put("so", "whois.nic.so");
		list.put("su", "whois.tcinet.ru");
		list.put("tc", "whois.adamsnames.tc");
		list.put("tel", "whois.nic.tel");
		list.put("tf", "whois.nic.tf");
		list.put("th", "whois.thnic.net");
		list.put("tj", "whois.nic.tj");
		list.put("tk", "whois.nic.tk");
		list.put("tl", "whois.domains.tl");
		list.put("tm", "whois.nic.tm");
		list.put("tn", "whois.ati.tn");
		list.put("to", "whois.tonic.to");
		list.put("tp", "whois.domains.tl");
		list.put("tr", "whois.nic.tr");
		list.put("travel", "whois.nic.travel");
		list.put("tw", "whois.twnic.net.tw");
		list.put("tv", "whois.nic.tv");
		list.put("tz", "whois.tznic.or.tz");
		list.put("ua", "whois.ua");
		list.put("uk", "whois.nic.uk");
		list.put("uk.com", "whois.centralnic.com");
		list.put("uk.net", "whois.centralnic.com");
		list.put("gov.uk", "whois.ja.net");
		list.put("us", "whois.nic.us");
		list.put("us.com", "whois.centralnic.com");
		list.put("uy", "nic.uy");
		list.put("uy.com", "whois.centralnic.com");
		list.put("uz", "whois.cctld.uz");
		list.put("va", "whois.ripe.net");
		list.put("vc", "whois2.afilias-grs.net");
		list.put("ve", "whois.nic.ve");
		list.put("vg", "whois.adamsnames.tc");
		list.put("ws", "whois.website.ws");
		list.put("xxx", "whois.nic.xxx");
		list.put("yu", "whois.ripe.net");
		list.put("za.com", "whois.centralnic.com");
	}

	public void run() 
	{
		try
		{
			String tld = getTld(request.getUrl());
			String server;

			if(list.containsKey(tld))
			{
				server = list.get(tld);
			}
			else
			{
				server = WhoisClient.DEFAULT_HOST;
			}



			// connect
			whois.connect(server);

			/*whois.addProtocolCommandListener(new ProtocolCommandListener(){

				public void protocolCommandSent(ProtocolCommandEvent e)
				{
					//stringRequest+= e.getCommand() + "\n";
				}

				public void protocolReplyReceived(ProtocolCommandEvent e)
				{
					//stringResponse+= e.getCommand() + "\n";
				}

			});*/
			
			// send query
			String response = whois.query(request.getUrl().getHost());

			// create response
			this.request.setContent(stringRequest);
			this.response = new Response(response);

			// call callback
            callback.onResponse(this.request, this.response);
		}
		catch(Exception e)
		{
			Aletheia.handleException(e);
		}
		finally
		{
			// disconnect
			try
			{
				whois.disconnect();
			}
			catch(IOException e)
			{
				Aletheia.handleException(e);
			}
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
		return new WhoisURLStreamHandler();
	}
	
	protected String getTld(URL url)
	{
		int pos = url.getHost().indexOf('.');
		
		if(pos != -1)
		{
			return url.getHost().substring(pos + 1);
		}
		else
		{
			return url.getHost();			
		}
	}
}
