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

package com.k42b3.aletheia.protocol.http;

import java.net.Socket;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.protocol.CallbackInterface;
import com.k42b3.aletheia.protocol.ProtocolAbstract;

/**
 * Http
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class HttpProtocol extends ProtocolAbstract
{
	public final static String newLine = "\r\n";
	public final static String type = "HTTP/1.1";
	public final static String method = "GET";

	private HttpParams params;
	private HttpHost host;
	private HttpProcessor httpproc;
	private HttpRequestExecutor httpexecutor;
	private HttpContext context;
	private ConnectionReuseStrategy connStrategy;
	private DefaultHttpClientConnection conn;

	public HttpProtocol()
	{
		// http settings
		params = new SyncBasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, true);

		HttpRequestInterceptor[] interceptors = {
			new RequestContent(),
			new RequestTargetHost(),
			new RequestConnControl(),
			new RequestExpectContinue()
		};

		httpproc = new ImmutableHttpProcessor(interceptors);
		httpexecutor = new HttpRequestExecutor();
	}

	public void setRequest(com.k42b3.aletheia.protocol.Request request, CallbackInterface callback) throws Exception
	{
		super.setRequest(request, callback);

		// request settings
		int port = request.getUrl().getPort();
		context = new BasicHttpContext(null);
		host = new HttpHost(request.getUrl().getHost(), port == -1 ? 80 : port);

		conn = new DefaultHttpClientConnection();
		connStrategy = new DefaultConnectionReuseStrategy();

		context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);
	}

	public void run()
	{
		try
		{
			Socket socket = new Socket(host.getHostName(), host.getPort());
			conn.bind(socket, params);

			// build request
			BasicHttpRequest request;

			if(!this.getRequest().getBody().isEmpty())
			{
				request = new BasicHttpEntityEnclosingRequest(this.getRequest().getMethod(), this.getRequest().getPath());
			}
			else
			{
				request = new BasicHttpRequest(this.getRequest().getMethod(), this.getRequest().getPath());
			}

			// add headers
			Map<String, String> headers = this.getRequest().getHeaders();
			Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();

			while(it.hasNext()) 
			{
				Map.Entry<String, String> pairs = it.next();

				request.addHeader(pairs.getKey(), pairs.getValue());
			}

			// set body
			if(request instanceof BasicHttpEntityEnclosingRequest)
			{
				StringEntity body = new StringEntity(this.getRequest().getBody());

				((BasicHttpEntityEnclosingRequest) request).setEntity(body);
			}

			logger.info("> " + request.getRequestLine().getUri());

			// request
			request.setParams(params);
			httpexecutor.preProcess(request, httpproc, context);

			HttpResponse response = httpexecutor.execute(request, conn, context);
			response.setParams(params);
			httpexecutor.postProcess(response, httpproc, context);

			logger.info("< " + response.getStatusLine());

			// set all request headers 
			Header[] allHeaders = request.getAllHeaders();

			for(int i = 0; i < allHeaders.length; i++)
			{
				this.getRequest().setHeader(allHeaders[i].getName(), allHeaders[i].getValue());
			}

			// create response
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
			try
			{
				conn.close();
			}
			catch(Exception e)
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
		return new HttpURLStreamHandler();
	}
}
