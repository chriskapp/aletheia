package com.k42b3.aletheia;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.k42b3.aletheia.protocol.Response;
import com.k42b3.aletheia.sidebar.HtmlResource;

public class Sidebar extends JPanel
{
	private HtmlResource htmlResource;

	public Sidebar()
	{
		super();

		// settings
		this.setPreferredSize(new Dimension(240, 400));
		this.setLayout(new CardLayout());
		this.setBorder(new EmptyBorder(4, 0, 4, 4));
		this.setVisible(false);

		// html resources
		this.htmlResource = new HtmlResource();
		this.add(this.htmlResource, "HtmlResource");
	}

	public void update(Response response)
	{
		try
		{
			if(response instanceof com.k42b3.aletheia.protocol.http.Response)
			{
				com.k42b3.aletheia.protocol.http.Response httpResponse = (com.k42b3.aletheia.protocol.http.Response) response;
				
				if(httpResponse.getHeader("Content-Type").indexOf("text/html") != -1)
				{
					this.htmlResource.process(httpResponse);

					CardLayout cl = (CardLayout) this.getLayout();
					cl.show(this, "HtmlResource");

					this.setVisible(true);
				}
				else
				{
					// no sidebar available for this content type
					this.setVisible(false);
				}
			}
			else
			{
				// no sidebar available for this response type
				this.setVisible(false);
			}
		}
		catch(Exception e)
		{
			Aletheia.handleException(e);
		}
	}
}
