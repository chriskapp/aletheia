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

package com.k42b3.aletheia.protocol.ftp;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.protocol.ProtocolAbstract;

/**
 * FtpProtocol
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class FtpProtocol extends ProtocolAbstract
{
	protected FTPClient ftp;

	public FtpProtocol()
	{
		ftp = new FTPClient();
		ftp.addProtocolCommandListener(new CommandListener());
		ftp.setListHiddenFiles(true);
	}

	public void run() 
	{
		try
		{
			// get commands
			String[] commands = this.getRequest().getContent().split("\n");

			// connect
			int port = request.getUrl().getPort() == -1 ? 21 : request.getUrl().getPort();

			ftp.connect(request.getUrl().getHost(), port);

			if(!FTPReply.isPositiveCompletion(ftp.getReplyCode()))
			{
				throw new Exception("FTP server refused connection");
			}

			// authenticate
			String auth = request.getUrl().getUserInfo();
			boolean loginResult = false;

			if(auth != null)
			{
				String[] user = auth.split(":");

				if(user.length == 2)
				{
					loginResult = ftp.login(user[0], user[1]);
				}
				else
				{
					throw new Exception("Invalid credentials");
				}
			}
			else
			{
				loginResult = ftp.login("anonymous", "anonymous");
			}

			if(!loginResult)
			{
				ftp.logout();

				throw new Exception("Not authenticated");
			}

			// enter passive mode
			ftp.enterRemotePassiveMode();

			// set type
			ftp.setFileType(FTP.ASCII_FILE_TYPE);

			// change working directory
			String path = request.getUrl().getPath();
			path = path.isEmpty() ? "/" : path;

			ftp.changeWorkingDirectory(path);

			// send commands
			for(int i = 0; i < commands.length; i++)
			{
				String request = commands[i].trim();

				if(!request.isEmpty())
				{
					String[] parts = request.split(" ", 2);
					String cmd = parts[0].toUpperCase();
					String args = parts.length == 2 ? parts[1] : null;

					this.sendCommad(cmd, args);
				}
			}

			// logout
			ftp.noop();
			ftp.logout();

			// create response
			this.response = new Response(Aletheia.getInstance().getActiveOut().getText());

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
				if(ftp.isConnected())
				{
					ftp.disconnect();
				}
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
		return new FtpURLStreamHandler();
	}

	private void sendCommad(String cmd, String args) throws Exception
	{
		if(cmd.equals("LIST") || cmd.equals("MLSD"))
		{
			FTPFile[] files;

			if(cmd.equals("MLSD"))
			{
				files = ftp.mlistDir(args);
			}
			else
			{
				files = ftp.listFiles(args);
			}

			for(FTPFile file : files)
			{
				if(file != null)
				{
					Aletheia.getInstance().getActiveOut().append(file.getRawListing());
				}
			}
		}
		else if(cmd.equals("NLST"))
		{
			String[] names = ftp.listNames(args);

			if(names != null)
			{
				for(String name : names)
				{
					Aletheia.getInstance().getActiveOut().append(name);
				}
			}
		}
		else
		{
			if(!ftp.doCommand(cmd, args))
			{
				throw new Exception(ftp.getReplyString());
			}
		}
	}

	private class CommandListener implements ProtocolCommandListener
	{
		public void protocolCommandSent(ProtocolCommandEvent e) 
		{
			String msg = e.getMessage().trim();

			logger.info("> " + msg);

			Aletheia.getInstance().getActiveOut().append(msg);
		}

		public void protocolReplyReceived(ProtocolCommandEvent e)
		{
			String msg = e.getMessage().trim();

			logger.info("< " + msg);

			Aletheia.getInstance().getActiveOut().append(msg);
		}
	}
}
