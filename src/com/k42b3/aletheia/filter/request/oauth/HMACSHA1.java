/**
 * $Id: HMACSHA1.java 18 2012-05-27 13:15:41Z k42b3.x@googlemail.com $
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

package com.k42b3.aletheia.filter.request.oauth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.k42b3.aletheia.Aletheia;

/**
 * HMACSHA1
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 * @version    $Revision: 18 $
 */
public class HMACSHA1 implements SignatureInterface
{
	public String build(String baseString, String consumerSecret, String tokenSecret)
	{
		try
		{
			String key = Util.urlEncode(consumerSecret) + "&" + Util.urlEncode(tokenSecret);

			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret = new SecretKeySpec(key.getBytes(), "HmacSHA1");
			mac.init(secret);
			byte[] result = mac.doFinal(baseString.getBytes());
			
			return Base64.encodeBase64String(result);
		}
		catch(Exception e)
		{
			Aletheia.handleException(e);

			return null;
		}
	}
}
