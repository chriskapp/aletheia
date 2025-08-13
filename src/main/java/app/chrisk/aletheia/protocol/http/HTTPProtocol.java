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

package app.chrisk.aletheia.protocol.http;

import app.chrisk.aletheia.Aletheia;
import app.chrisk.aletheia.protocol.CallbackInterface;
import app.chrisk.aletheia.protocol.ProtocolAbstract;
import org.apache.http.*;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.*;

import java.net.Socket;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * HTTPProtocol
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class HTTPProtocol extends ProtocolAbstract
{
	public final static String newLine = "\r\n";
	public final static String type = "HTTP/1.1";
	public final static String method = "GET";

	protected HttpParams params;
	protected HttpHost host;
	protected HttpProcessor httpProcessor;
	protected HttpRequestExecutor httpExecutor;
	protected HttpContext context;
	protected ConnectionReuseStrategy connectionStrategy;
	protected DefaultHttpClientConnection connection;

	public HTTPProtocol()
	{
		params = new SyncBasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, true);

		HttpRequestInterceptor[] interceptors = {
			new RequestContent(true),
			new RequestTargetHost(),
			new RequestConnControl(),
			new RequestExpectContinue()
		};

		httpProcessor = new ImmutableHttpProcessor(interceptors);
		httpExecutor = new HttpRequestExecutor();
	}

	public void setRequest(app.chrisk.aletheia.protocol.Request request, CallbackInterface callback) throws Exception
	{
		super.setRequest(request, callback);

		// request settings
		int port = request.getUrl().getPort();

		if (port == -1) {
			if (request.getUrl().getProtocol().equalsIgnoreCase("https")) {
				port = 443;
			} else {
				port = 80;
			}
		}

		context = new BasicHttpContext(null);
		host = new HttpHost(request.getUrl().getHost(), port);

		connection = new DefaultHttpClientConnection();
		connectionStrategy = new DefaultConnectionReuseStrategy();

		context.setAttribute(ExecutionContext.HTTP_CONNECTION, connection);
		context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);
	}

	public void run()
	{
		try {
			Socket socket = this.getSocket();
			connection.bind(socket, params);

			BasicHttpRequest request;
			if (!this.getRequest().getBody().isEmpty()) {
				request = new BasicHttpEntityEnclosingRequest(this.getRequest().getMethod(), this.getRequest().getPath());
			} else {
				request = new BasicHttpRequest(this.getRequest().getMethod(), this.getRequest().getPath());
			}

			// add headers
			String boundary = null;
			ArrayList<String> ignoreHeader = new ArrayList<>();
			ignoreHeader.add("Content-Length");
			ignoreHeader.add("Expect");

			LinkedList<Header> headers = this.getRequest().getHeaders();

            for (Header value : headers) {
                if (!ignoreHeader.contains(value.getName())) {
                    // if the content-type header gets set the conent-length
                    // header is automatically added
                    request.addHeader(value);
                }

                if (value.getName().equals("Content-Type") && value.getValue().startsWith("multipart/form-data")) {
                    String header = value.getValue().substring(value.getValue().indexOf(";") + 1).trim();

                    if (!header.isEmpty()) {
                        String[] parts = header.split("=");

                        if (parts.length >= 2) {
                            boundary = parts[1];
                        }
                    }
                }
            }

			if (request instanceof BasicHttpEntityEnclosingRequest && boundary != null) {
				boundary = "--" + boundary;
				StringBuilder body = new StringBuilder();
				String req = this.getRequest().getBody();

				int i = 0;
				String partHeader;
				String partBody;

				while ((i = req.indexOf(boundary, i)) != -1) {
					int hPos = req.indexOf("\n\n", i + 1);
					if (hPos != -1) {
						partHeader = req.substring(i + boundary.length() + 1, hPos).trim();
					} else {
						partHeader = null;
					}

					int bpos = req.indexOf(boundary, i + 1);
					if (bpos != -1) {
						partBody = req.substring(hPos == -1 ? i : hPos + 2, bpos);
					} else {
						partBody = req.substring(hPos == -1 ? i : hPos + 2);
					}

					if (partBody.equals(boundary + "--")) {
						body.append(boundary).append("--").append("\r\n");
						break;
					}
					else if (!partBody.isEmpty()) {
						body.append(boundary).append("\r\n");
						if (partHeader != null && !partHeader.isEmpty()) {
							body.append(partHeader.replaceAll("\n", "\r\n"));
							body.append("\r\n");
							body.append("\r\n");
						}
						body.append(partBody);
					}

					i++;
				}

				this.getRequest().setBody(body.toString().replaceAll("\r\n", "\n"));

				HttpEntity entity = new StringEntity(this.getRequest().getBody());

				((BasicHttpEntityEnclosingRequest) request).setEntity(entity);
			} else if (request instanceof BasicHttpEntityEnclosingRequest) {
				HttpEntity entity = new StringEntity(this.getRequest().getBody());

				((BasicHttpEntityEnclosingRequest) request).setEntity(entity);
			}

			logger.info("> " + request.getRequestLine().getUri());

			// request
			request.setParams(params);
			httpExecutor.preProcess(request, httpProcessor, context);

			HttpResponse response = httpExecutor.execute(request, connection, context);
			response.setParams(params);
			httpExecutor.postProcess(response, httpProcessor, context);

			logger.info("< " + response.getStatusLine());

			LinkedList<Header> header = new LinkedList<>();
			Header[] allHeaders = request.getAllHeaders();

            Collections.addAll(header, allHeaders);

			this.getRequest().setHeaders(header);

			this.response = new Response(response);

			callback.onResponse(this.request, this.response);
		} catch(Exception e) {
			Aletheia.handleException(e);
		} finally {
			try {
				connection.close();
			} catch(Exception e) {
				Aletheia.handleException(e);
			}
		}
	}

	public Request buildRequest(URL url, String content) {
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
		return new HTTPURLStreamHandler();
	}

	public Socket getSocket() throws Exception
	{
		return new Socket(host.getHostName(), host.getPort());
	}
}
