/**
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

package com.k42b3.aletheia.protocol.https;

import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.k42b3.aletheia.CertificateStore;
import com.k42b3.aletheia.protocol.http.HttpProtocol;

/**
 * HttpsProtocol
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class HttpsProtocol extends HttpProtocol
{
	public Socket getSocket() throws Exception
	{
		TrustManager[] trustAllCerts = new TrustManager[] { 
			new CrazyX509TrustManager()
		};

		SSLContext sc = SSLContext.getInstance("SSL");
	    sc.init(null, trustAllCerts, new SecureRandom());

		return sc.getSocketFactory().createSocket(host.getHostName(), host.getPort());
	}

	class CrazyX509TrustManager implements X509TrustManager
	{
		public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException
		{
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException
		{
			CertificateStore.getInstance().clear();

			if(certs != null)
			{
				for(int i = 0; i < certs.length; i++)
				{
					CertificateStore.getInstance().addCertificate(certs[i]);
				}
			}
		}

		public X509Certificate[] getAcceptedIssuers()
		{
			return new X509Certificate[0];
		}
	}
}
