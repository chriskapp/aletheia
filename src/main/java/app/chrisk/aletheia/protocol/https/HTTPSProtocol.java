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

package app.chrisk.aletheia.protocol.https;

import app.chrisk.aletheia.CertificateStore;
import app.chrisk.aletheia.protocol.http.HTTPProtocol;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * HTTPSProtocol
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class HTTPSProtocol extends HTTPProtocol
{
	public Socket getSocket() throws Exception
	{
		TrustManager[] trustAllCerts = new TrustManager[] {
            new CrazyX509TrustManager()
		};

		SSLContext sc = SSLContext.getInstance("TLS");
	    sc.init(null, trustAllCerts, new SecureRandom());

		return sc.getSocketFactory().createSocket(host.getHostName(), host.getPort());
	}

	static class CrazyX509TrustManager implements X509TrustManager
	{
		public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException
		{
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException
		{
			CertificateStore.getInstance().clear();

			if (certs != null) {
                for (X509Certificate cert : certs) {
                    CertificateStore.getInstance().addCertificate(cert);
                }
			}
		}

		public X509Certificate[] getAcceptedIssuers()
		{
			return new X509Certificate[0];
		}
	}
}
