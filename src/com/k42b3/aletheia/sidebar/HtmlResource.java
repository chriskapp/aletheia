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

package com.k42b3.aletheia.sidebar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.Parser;
import com.k42b3.aletheia.TextFieldUrl;
import com.k42b3.aletheia.protocol.http.Util;

/**
 * Link
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class HtmlResource extends JPanel
{
	private ArrayList<String> resources;
	private DefaultListModel<String> lm;
	private JList<String> list;
	private JTextField search;

	private String baseUrl;

	public HtmlResource()
	{
		super();

		// settings
		this.setLayout(new BorderLayout());

		// search
		JPanel panelSearch = new JPanel();
		panelSearch.setLayout(new BorderLayout());
		
		this.search = new TextFieldUrl();
		this.search.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) 
			{
			}

			public void keyReleased(KeyEvent e) 
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					if(!list.hasFocus())
					{
						list.requestFocus();
						list.setSelectedIndex(0);
					}
				}
				else
				{
					filter(search.getText());
				}
			}

			public void keyPressed(KeyEvent e) 
			{
			}

		});
		this.search.setPreferredSize(new Dimension(200, 24));

		panelSearch.add(this.search, BorderLayout.CENTER);

		this.add(panelSearch, BorderLayout.SOUTH);


		// link list
		resources = new ArrayList<String>();
		lm = new DefaultListModel<String>();
		list = new JList<String>(lm);
		list.setFont(new Font("Courier New", Font.PLAIN, 12));
		list.setBackground(new Color(255, 255, 255));
		list.setForeground(new Color(0, 0, 0));
		//list.addListSelectionListener(new LinkListener());
		list.addKeyListener(new LinkKeyListener());
		list.addMouseListener(new LinkMouseListener());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scp = new JScrollPane(list);
		scp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//scp.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));

		this.add(scp, BorderLayout.CENTER);
	}

	public void process(com.k42b3.aletheia.protocol.http.Response response) throws Exception
	{
		// get content
		String html = response.getBody();

		// set base url
		this.baseUrl = Aletheia.getInstance().getActiveUrl().getText();

		// clear
		lm.clear();
		resources.clear();

		// parse html
		this.parseLinks(html);
		this.parseImages(html);
		this.parseScripts(html);

		// call filter
		SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{
				filter("");
			}

		});
	}

	private void parseLinks(String html)
	{
		// parse links
		ArrayList<String> links = new ArrayList<String>();
		links.add("-- LINKS --");
		
		for(int i = 0; i < html.length(); i++)
		{
			if(Parser.startsWith("<a", i, html))
			{
				String aTag = Parser.getTag(i, html);
				String href = Parser.getAttribute("href", aTag);

				if(href != null && !href.isEmpty())
				{
					links.add(href);
				}
			}
		}

		// add links to list
		if(links.size() > 0)
		{
			resources.addAll(links);
		}
	}

	private void parseImages(String html)
	{
		// parse links
		ArrayList<String> images = new ArrayList<String>();
		images.add("-- IMAGES --");

		for(int i = 0; i < html.length(); i++)
		{
			if(Parser.startsWith("<img", i, html))
			{
				String aTag = Parser.getTag(i, html);
				String src = Parser.getAttribute("src", aTag);

				if(src != null && !src.isEmpty())
				{
					images.add(src);
				}
			}
		}

		// add links to list
		if(images.size() > 0)
		{
			resources.addAll(images);
		}
	}

	private void parseScripts(String html)
	{
		// parse links
		ArrayList<String> scripts = new ArrayList<String>();
		scripts.add("-- SCRIPTS --");

		for(int i = 0; i < html.length(); i++)
		{
			if(Parser.startsWith("<script", i, html))
			{
				String aTag = Parser.getTag(i, html);
				String src = Parser.getAttribute("src", aTag);

				if(src != null && !src.isEmpty())
				{
					scripts.add(src);
				}
			}
		}

		// add links to list
		if(scripts.size() > 0)
		{
			resources.addAll(scripts);
		}
	}

	private void filter(String text)
	{
		if(text != null && !text.isEmpty())
		{
			for(int i = 0; i < lm.size(); i++)
			{
				if(!lm.get(i).startsWith("--") && lm.get(i).indexOf(text) == -1)
				{
					lm.remove(i);
				}
			}
		}
		else
		{
			lm.clear();

			for(int i = 0; i < resources.size(); i++)
			{
				lm.addElement(resources.get(i));
			}
		}
	}

	private void callSelectedLink(boolean newTab)
	{
		String selectedUrl = list.getSelectedValue();

		if(selectedUrl != null && !selectedUrl.startsWith("--"))
		{
			try
			{
				String url = Util.resolveHref(baseUrl, selectedUrl);

				if(newTab)
				{
					Aletheia.getInstance().newTab(true);
				}

				Aletheia.getInstance().run(url);
			}
			catch(Exception e)
			{
				Aletheia.handleException(e);
			}
		}
	}

	class LinkMouseListener implements MouseListener
	{
		public void mouseClicked(MouseEvent e) 
		{
		}

		public void mouseEntered(MouseEvent e) 
		{
		}

		public void mouseExited(MouseEvent e) 
		{
		}

		public void mousePressed(MouseEvent e) 
		{
			callSelectedLink(e.isControlDown());
		}

		public void mouseReleased(MouseEvent e) 
		{
		}
	}

	class LinkKeyListener implements KeyListener
	{
		public void keyTyped(KeyEvent e)
		{
		}

		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				callSelectedLink(e.isControlDown());
			}
			else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				setVisible(false);
			}
		}

		public void keyPressed(KeyEvent e) 
		{
		}
	}
}
