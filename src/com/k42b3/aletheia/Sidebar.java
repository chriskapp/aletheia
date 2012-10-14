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

package com.k42b3.aletheia;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.k42b3.aletheia.protocol.Response;
import com.k42b3.aletheia.sidebar.SidebarInterface;
import com.k42b3.aletheia.sidebar.http.Atom;
import com.k42b3.aletheia.sidebar.http.Html;

/**
 * Sidebar
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Sidebar extends JPanel
{
	private Html html;
	private Atom atom;

	public Sidebar()
	{
		super();

		// settings
		this.setPreferredSize(new Dimension(240, 400));
		this.setLayout(new CardLayout());
		this.setBorder(new EmptyBorder(4, 0, 4, 4));
		this.setVisible(false);

		// add sidebar panels

		// http
		// html
		this.html = new Html();
		this.add(this.html, this.html.getContentType());

		// atom
		this.atom = new Atom();
		this.add(this.atom, this.atom.getContentType());
	}

	public void update(Response response)
	{
		try
		{
			if(response instanceof com.k42b3.aletheia.protocol.http.Response)
			{
				com.k42b3.aletheia.protocol.http.Response httpResponse = (com.k42b3.aletheia.protocol.http.Response) response;

				Component[] components = this.getComponents();
				String contentType = httpResponse.getHeader("Content-Type");
				
				if(contentType != null)
				{
					for(int i = 0; i < components.length; i++)
					{
						JPanel panel = (JPanel) components[i];

						if(panel != null && contentType.indexOf(panel.getName()) != -1)
						{
							((SidebarInterface) components[i]).process(response);

							CardLayout cl = (CardLayout) this.getLayout();
							cl.show(this, components[i].getName());

							this.setVisible(true);

							return;
						}
					}
				}

				// no sidebar available for this content type
				this.setVisible(false);
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
