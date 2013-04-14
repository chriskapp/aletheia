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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextPane;

import com.k42b3.aletheia.protocol.Response;

/**
 * TextPaneOut
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class TextPaneOut extends JTextPane
{
	private Response response;

	public TextPaneOut()
	{
		this.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.setEditable(false);
		this.setBackground(new Color(255, 255, 255));
		this.setForeground(new Color(0, 0, 0));
	}

	public boolean getScrollableTracksViewportWidth()
	{
		return getUI().getPreferredSize(this).width <= getParent().getSize().width;
	}

	public void setResponse(Response response)
	{
		this.response = response;

		this.setText(response.toString());
	}

	public boolean hasRequest()
	{
		return this.response != null;
	}

	public void setBody(String body)
	{
		int pos = this.getText().indexOf("\n\n");
		
		if(pos != -1)
		{
			this.setText(this.getText().substring(0, pos) + "\n\n" + body);
		}
	}

	public Response getResponse()
	{
		return this.response;
	}

	public void update()
	{
		this.setText(this.response.toString());
	}

	public void append(String line)
	{
		this.setText(this.getText().trim() + "\n" + line);
	}
}
