package com.k42b3.aletheia.sidebar.http;

import javax.swing.JPanel;

import com.k42b3.aletheia.sidebar.SidebarInterface;

public abstract class SidebarHttpAbstract extends JPanel implements SidebarInterface
{
	abstract public String getContentType();
	abstract public void process(com.k42b3.aletheia.protocol.http.Response response) throws Exception;

	public SidebarHttpAbstract()
	{
		super();

		this.setName(this.getContentType());
	}

	public void process(com.k42b3.aletheia.protocol.Response response) throws Exception
	{
		if(response instanceof com.k42b3.aletheia.protocol.http.Response)
		{
			com.k42b3.aletheia.protocol.http.Response httpResponse = (com.k42b3.aletheia.protocol.http.Response) response;

			this.process(httpResponse);
		}
	}
}
