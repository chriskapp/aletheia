package app.chrisk.aletheia.oauth;

import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * OAuth
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class OAuth
{
	public static String buildAuthString(HashMap<String, String> values)
	{
		StringBuilder authString = new StringBuilder();

		Iterator<Entry<String, String>> it = values.entrySet().iterator();

		while(it.hasNext())
		{
			Entry<String, String> e = it.next();

			authString.append(urlEncode(e.getKey()) + "=\"" + urlEncode(e.getValue()) + "\", ");
		}

		String str = authString.toString();


		// remove ", " from string
		str = str.substring(0, str.length() - 2);


		return str;
	}

	public static String buildBaseString(String requestMethod, String url, HashMap<String, String> params)
	{
		StringBuilder base = new StringBuilder();

		base.append(urlEncode(getNormalizedMethod(requestMethod)));

		base.append('&');

		base.append(urlEncode(getNormalizedUrl(url)));

		base.append('&');

		base.append(urlEncode(getNormalizedParameters(params)));

		return base.toString();
	}

	public static String getTimestamp()
	{
		return "" + (System.currentTimeMillis() / 1000);
	}

	public static String getNonce()
	{
		try
		{
			byte[] nonce = new byte[32];

			Random rand;

			rand = SecureRandom.getInstance("SHA1PRNG");

			rand.nextBytes(nonce);


			return DigestUtils.md5Hex(rand.toString());
		}
		catch(Exception e)
		{
			return DigestUtils.md5Hex("" + System.currentTimeMillis());
		}
	}

	public static String getVersion()
	{
		return "1.0";
	}

	public static SignatureInterface getSignature(String method) throws Exception
	{
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("PLAINTEXT", "PLAINTEXT");
		map.put("HMAC-SHA1", "HMACSHA1");

		if(map.containsKey(method))
		{
			String cls = "com.k42b3.aletheia.oauth." + map.get(method);
			Class<SignatureInterface> c = (Class<SignatureInterface>) Class.forName(cls);

			return c.newInstance();
		}
		else
		{
			throw new Exception("Invalid signature method");
		}
	}

	public static String urlEncode(String content)
	{
		try
		{
			String encoded = URLEncoder.encode(content, "UTF-8");

			encoded = encoded.replaceAll("%7E", "~");
					
			return encoded;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
	
	protected static String getNormalizedParameters(HashMap<String, String> params)
	{
		Iterator<Entry<String, String>> it = params.entrySet().iterator();

		List<String> keys = new ArrayList<String>();

		while(it.hasNext())
		{
			Entry<String, String> e = it.next();

			keys.add(e.getKey());
		}


		// sort params
		Collections.sort(keys);


		// build normalized params
		StringBuilder normalizedParams = new StringBuilder();

		for(int i = 0; i < keys.size(); i++)
		{
			normalizedParams.append(urlEncode(keys.get(i)) + "=" + urlEncode(params.get(keys.get(i))) + "&");
		}

		String str = normalizedParams.toString();


		// remove trailing &
		str = str.substring(0, str.length() - 1);


		return str;
	}

	protected static String getNormalizedUrl(String rawUrl)
	{
		try
		{
			rawUrl = rawUrl.toLowerCase();

			URL url = new URL(rawUrl);

			int port = url.getPort();

			if(port == -1 || port == 80 || port == 443)
			{
				return url.getProtocol() + "://" + url.getHost() + url.getPath();
			}
			else
			{
				return url.getProtocol() + "://" + url.getHost() + ":" + port + url.getPath();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();

			return null;
		}
	}

	protected static String getNormalizedMethod(String method)
	{
		return method.toUpperCase();
	}
}
