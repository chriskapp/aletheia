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

package com.k42b3.aletheia.sidebar.http;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.TextFieldUrl;

/**
 * Atom
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Atom extends SidebarHttpAbstract
{
	private ArrayList<Entry> resources;
	private DefaultListModel<Entry> lm;
	private JList<Entry> list;
	private JTextField search;

	public Atom()
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


		// list
		resources = new ArrayList<Entry>();
		lm = new DefaultListModel<Entry>();
		list = new JList<Entry>(lm);
		list.setFont(new Font("Courier New", Font.PLAIN, 12));
		list.setBackground(new Color(255, 255, 255));
		list.setForeground(new Color(0, 0, 0));
		list.addKeyListener(new LinkKeyListener());
		list.addMouseListener(new LinkMouseListener());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new ListCellRenderer<Entry>() {

			public Component getListCellRendererComponent(JList<? extends Entry> list, Entry value, int index, boolean isSelected, boolean cellHasFocus)
			{
				String html = "<html>";
				if(!value.getTitle().isEmpty())
				{
					html+= "&nbsp;<font color=gray size=-1>" + value.getTitle() + "</font><br />";
				}
				html+= "&nbsp;" + value.getUrl();
				html+= "</html>";

				JLabel label = new JLabel();
				label.setFont(new Font("Monospaced", Font.PLAIN, 12));
				label.setOpaque(true);
				label.setText(html);
				label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

				if(isSelected)
				{
					label.setBackground(SystemColor.activeCaption);
					label.setForeground(SystemColor.textHighlightText);
				}
				else
				{
					label.setBackground(SystemColor.window);
					label.setForeground(SystemColor.textText);
				}

				return label;
			}

		});

		JScrollPane scp = new JScrollPane(list);
		scp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		this.add(scp, BorderLayout.CENTER);
	}

	public String getContentType()
	{
		return "application/atom+xml";
	}

	public void process(com.k42b3.aletheia.protocol.http.Response response) throws Exception
	{
		// get content
		String xml = response.getBody();

		// clear
		lm.clear();
		resources.clear();

		// parse html
		this.parseEntries(xml);

		// call filter
		SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{
				filter("");
			}

		});
	}

	private void parseEntries(String xml) throws Exception
	{
		// read xml
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));

		Document doc = db.parse(is);

		NodeList entries = doc.getElementsByTagName("entry");

		for(int i = 0; i < entries.getLength(); i++)
		{
			Element entry = (Element) entries.item(i);
			Element title = (Element) entry.getElementsByTagName("title").item(0);
			Element link = (Element) entry.getElementsByTagName("link").item(0);

			if(title != null && link != null)
			{
				resources.add(new Entry(title.getTextContent(), link.getAttribute("href")));
			}
		}
	}

	private void filter(String text)
	{
		if(text != null && !text.isEmpty())
		{
			for(int i = 0; i < lm.size(); i++)
			{
				if(lm.get(i).getTitle().toLowerCase().indexOf(text.toLowerCase()) == -1)
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
		Entry selectedUrl = list.getSelectedValue();

		if(selectedUrl != null)
		{
			try
			{
				String url = selectedUrl.getUrl();

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
	
	class Entry
	{
		private String title;
		private String url;
		
		public Entry(String title, String url)
		{
			this.title = title;
			this.url = url;
		}

		public String getTitle()
		{
			return title;
		}

		public void setTitle(String title)
		{
			this.title = title;
		}

		public String getUrl()
		{
			return url;
		}

		public void setUrl(String url)
		{
			this.url = url;
		}
		
		public String toString()
		{
			return this.title;
		}
	}
}
