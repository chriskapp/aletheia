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

package app.chrisk.aletheia.sidebar.http;

import app.chrisk.aletheia.Aletheia;
import app.chrisk.aletheia.TextFieldUrl;
import app.chrisk.aletheia.protocol.http.Response;
import app.chrisk.aletheia.protocol.http.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * HTML
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class HTML extends SidebarHTTPAbstract
{
	private final ArrayList<Resource> resources;
	private final DefaultListModel<Resource> lm;
	private final JList<Resource> list;
	private final JTextField search;

	private String baseUrl;

	public HTML()
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
					String text = search.getText();

					if(search.getSelectedText() != null && search.getSelectedText().equals(search.getText()))
					{
						text = "";
					}

					filter(text);
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
		resources = new ArrayList<Resource>();
		lm = new DefaultListModel<Resource>();
		list = new JList<Resource>(lm);
		list.setFont(new Font("Courier New", Font.PLAIN, 12));
		list.setBackground(new Color(255, 255, 255));
		list.setForeground(new Color(0, 0, 0));
		list.addKeyListener(new LinkKeyListener());
		list.addMouseListener(new LinkMouseListener());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new ListCellRenderer<Resource>() {

			public Component getListCellRendererComponent(JList<? extends Resource> list, Resource value, int index, boolean isSelected, boolean cellHasFocus)
			{
				String html = "<html>";
				if(value.isHeading())
				{
					html+= "&nbsp;<b>" + value.getDescription() + "</b>";
				}
				else
				{
					if(!value.getDescription().isEmpty())
					{
						html+= "&nbsp;<font color=gray size=-1>" + value.getDescription() + "</font><br />";
					}

					html+= "&nbsp;" + value.getUrl();
				}
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
		return "text/html";
	}

	public void process(Response response) throws Exception
	{
		// get content
		String html = response.getBody();

		// set base url
		this.baseUrl = Aletheia.getInstance().getActiveUrl().getText();

		// clear
		lm.clear();
		resources.clear();

		// parse html
		Document doc = Jsoup.parse(html);

		this.parseLinks(doc);
		this.parseImages(doc);
		this.parseScripts(doc);
		this.parseObjects(doc);
		this.parseFrames(doc);

		// call filter
		SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{
				filter("");
			}

		});
	}

	private void parseLinks(Document doc)
	{
		ArrayList<Resource> links = new ArrayList<Resource>();
		Elements els = doc.getElementsByTag("a");

		links.add(new Resource(null, "-- LINKS --"));

		for(Element a : els)
		{
			String href = a.attr("href");
			if(!href.isEmpty())
			{
				links.add(new Resource(href, a.text()));
			}
		}

		if(links.size() > 1)
		{
			resources.addAll(links);
		}
	}

	private void parseImages(Document doc)
	{
		ArrayList<Resource> images = new ArrayList<Resource>();
		Elements els = doc.getElementsByTag("img");

		images.add(new Resource(null, "-- IMAGES --"));

		for(Element img : els)
		{
			String src = img.attr("src");
			if(!src.isEmpty())
			{
				images.add(new Resource(src, img.attr("alt")));
			}
		}

		if(images.size() > 1)
		{
			resources.addAll(images);
		}
	}

	private void parseScripts(Document doc)
	{
		ArrayList<Resource> scripts = new ArrayList<Resource>();
		Elements els = doc.getElementsByTag("script");

		scripts.add(new Resource(null, "-- SCRIPTS --"));

		for(Element script : els)
		{
			String src = script.attr("src");

			if(!src.isEmpty())
			{
				scripts.add(new Resource(src));
			}
		}

		if(scripts.size() > 1)
		{
			resources.addAll(scripts);
		}
	}

	private void parseObjects(Document doc)
	{
		ArrayList<Resource> objects = new ArrayList<Resource>();
		Elements els = doc.getElementsByTag("object");

		objects.add(new Resource(null, "-- OBJECTS --"));

		for(Element object : els)
		{
			String data = object.attr("data");
			if(!data.isEmpty())
			{
				objects.add(new Resource(data));
			}
		}

		els = doc.getElementsByTag("embed");

		for(Element embed : els)
		{
			String src = embed.attr("src");
			if(!src.isEmpty())
			{
				objects.add(new Resource(src));
			}
		}

		if(objects.size() > 1)
		{
			resources.addAll(objects);
		}
	}

	private void parseFrames(Document doc)
	{
		ArrayList<Resource> frames = new ArrayList<Resource>();
		Elements els = doc.getElementsByTag("frame");

		frames.add(new Resource(null, "-- FRAMES --"));

		for(Element frame : els)
		{
			String src = frame.attr("src");
			if(!src.isEmpty())
			{
				frames.add(new Resource(src));
			}
		}

		els = doc.getElementsByTag("iframe");

		for(Element iframe : els)
		{
			String src = iframe.attr("src");
			if(!src.isEmpty())
			{
				frames.add(new Resource(src));
			}
		}

		if(frames.size() > 1)
		{
			resources.addAll(frames);
		}
	}

	private synchronized void filter(String text)
	{
		if(text != null && !text.isEmpty())
		{
			for(int i = 0; i < lm.size(); i++)
			{
				if(!lm.get(i).isHeading() && (
						lm.get(i).getUrl().toLowerCase().indexOf(text.toLowerCase()) == -1 && 
								lm.get(i).getDescription().toLowerCase().indexOf(text.toLowerCase()) == -1))
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
		Resource selectedResource = list.getSelectedValue();

		if(selectedResource != null && !selectedResource.isHeading())
		{
			try
			{
				String url = Util.resolveHref(baseUrl, selectedResource.getUrl());

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
	
	class Resource
	{
		private String url;
		private String description;
		
		public Resource(String url, String description)
		{
			this.url = url;
			this.description = description;
		}

		public Resource(String url)
		{
			this(url, "");
		}

		public String getUrl()
		{
			return url;
		}
		
		public String getDescription()
		{
			return description;
		}
		
		public boolean isHeading()
		{
			return url == null;
		}
	}
}
