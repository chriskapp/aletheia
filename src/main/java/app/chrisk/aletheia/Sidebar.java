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

package app.chrisk.aletheia;

import app.chrisk.aletheia.protocol.http.Response;
import app.chrisk.aletheia.sidebar.SidebarInterface;
import app.chrisk.aletheia.sidebar.http.Atom;
import app.chrisk.aletheia.sidebar.http.HTML;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Sidebar
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Sidebar extends JPanel
{
    public Sidebar()
	{
		super();

		// settings
		this.setPreferredSize(new Dimension(240, 400));
		this.setLayout(new CardLayout());
		this.setBorder(new EmptyBorder(4, 0, 2, 4));
		this.setVisible(false);

		// add sidebar panels

        HTML html = new HTML();
		this.add(html, html.getContentType());

        Atom atom = new Atom();
		this.add(atom, atom.getContentType());
	}

	public void update(app.chrisk.aletheia.protocol.Response response)
	{
		try {
			if (response instanceof Response) {
				Response httpResponse = (Response) response;

				Component[] components = this.getComponents();
				String contentType = httpResponse.getHeader("Content-Type");
				
				if (contentType != null) {
                    for (Component component : components) {
                        JPanel panel = (JPanel) component;

                        if (panel != null && contentType.contains(panel.getName())) {
                            ((SidebarInterface) component).process(response);

                            CardLayout cl = (CardLayout) this.getLayout();
                            cl.show(this, component.getName());

                            this.setVisible(true);

                            return;
                        }
                    }
				}

				// no sidebar available for this content type
				this.setVisible(false);
			} else {
				// no sidebar available for this response type
				this.setVisible(false);
			}
		} catch(Exception e) {
			Aletheia.handleException(e);
		}
	}
}
