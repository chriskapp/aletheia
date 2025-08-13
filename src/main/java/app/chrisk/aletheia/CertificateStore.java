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

package app.chrisk.aletheia;

import java.security.cert.X509Certificate;
import java.util.LinkedList;

/**
 * CertificateStore
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class CertificateStore
{
	private static CertificateStore instance;

	private LinkedList<X509Certificate> certs = new LinkedList<X509Certificate>();

	public CertificateStore()
	{
	}

	public LinkedList<X509Certificate> getCertificates()
	{
		return certs;
	}

	public void addCertificate(X509Certificate cert)
	{
		if(!certs.contains(cert))
		{
			certs.add(cert);
		}
	}

	public void removeCertificate(X509Certificate cert)
	{
		certs.remove(cert);
	}

	public void clear()
	{
		certs.clear();
	}

	public static CertificateStore getInstance()
	{
		if(instance == null)
		{
			instance = new CertificateStore();
		}

		return instance;
	}
}
