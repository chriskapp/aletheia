/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more informations visit <http://aletheia.k42b3.com>
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * MenuBar
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class MenuBar extends JMenuBar
{
	protected MenuBarActionListener listener;
	protected Config config;

	public MenuBar(Config config)
	{
		super();

		this.config = config;

		buildUrl();
		buildRequest();
		buildResponse();
		buildBookmark();
		buildView();
	}

	public void setActionListener(MenuBarActionListener listener)
	{
		this.listener = listener;
	}

	protected void buildUrl()
	{
		JMenu menu = new JMenu("URL");

		JMenuItem itemRun = new JMenuItem("Run");
		itemRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		itemRun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onUrlRun();
			}

		});
		menu.add(itemRun);

		JMenuItem itemReset = new JMenuItem("Reset");
		itemReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		itemReset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onUrlReset();
			}

		});
		menu.add(itemReset);

		JMenuItem itemNewTab = new JMenuItem("New Tab");
		itemNewTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		itemNewTab.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onUrlNewTab();
			}

		});
		menu.add(itemNewTab);

		JMenuItem itemCloseTab = new JMenuItem("Close Tab");
		itemCloseTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		itemCloseTab.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onUrlCloseTab();
			}

		});
		menu.add(itemCloseTab);

		JMenuItem itemSave = new JMenuItem("Save");
		itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		itemSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				listener.onUrlSave();
			}

		});
		menu.add(itemSave);

		JMenuItem itemLoad = new JMenuItem("Open");
		itemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		itemLoad.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onUrlOpen();
			}

		});
		menu.add(itemLoad);

		JMenuItem itemBookmark = new JMenuItem("Bookmark");
		itemBookmark.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		itemBookmark.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onBookmark();
			}

		});
		menu.add(itemBookmark);
			
		JMenuItem itemFocus = new JMenuItem("Focus");
		itemFocus.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		itemFocus.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onUrlFocus();
			}

		});
		menu.add(itemFocus);

		this.add(menu);
	}
	
	protected void buildRequest()
	{
		JMenu menu = new JMenu("Request");

		JMenuItem itemBasicAuth = new JMenuItem("Basic Auth");
		itemBasicAuth.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onRequestBasicAuth();
			}

		});
		menu.add(itemBasicAuth);

		JMenuItem itemForm = new JMenuItem("Form");
		itemForm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onRequestForm();
			}

		});
		menu.add(itemForm);

		JMenuItem itemOauthRequestToken = new JMenuItem("Oauth Request Token");
		itemOauthRequestToken.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onRequestOauthRequestToken();
			}

		});
		menu.add(itemOauthRequestToken);

		JMenuItem itemPingback = new JMenuItem("Pingback");
		itemPingback.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onRequestPingback();
			}

		});
		menu.add(itemPingback);

		JMenuItem itemUpload = new JMenuItem("Upload");
		itemUpload.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onRequestUpload();
			}

		});
		menu.add(itemUpload);

		this.add(menu);
	}

	protected void buildResponse()
	{
		JMenu menu = new JMenu("Response");

		// html
		JMenu menuHtml = new JMenu("Html");

		// search
		JMenuItem itemSearch = new JMenuItem("Search");
		itemSearch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		itemSearch.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onResponseHtmlSearch();
			}

		});
		menuHtml.add(itemSearch);
		
		// form
		JMenuItem itemForm = new JMenuItem("Form");
		itemForm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		itemForm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onResponseHtmlForm();
			}

		});
		menuHtml.add(itemForm);

		// images
		JMenuItem itemImages = new JMenuItem("Images");
		itemImages.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		itemImages.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onResponseHtmlImages();
			}

		});
		menuHtml.add(itemImages);

		menu.add(menuHtml);

		// format
		JMenu menuFormat = new JMenu("Format");

		// html
		JMenuItem itemHtml = new JMenuItem("HTML");
		//itemJson.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		itemHtml.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onResponseFormatHtml();
			}

		});
		menuFormat.add(itemHtml);
		
		// json
		JMenuItem itemJson = new JMenuItem("JSON");
		//itemJson.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK));
		itemJson.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onResponseFormatJson();
			}

		});
		menuFormat.add(itemJson);

		// xml
		JMenuItem itemXml = new JMenuItem("XML");
		//itemXml.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		itemXml.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onResponseFormatXml();
			}

		});
		menuFormat.add(itemXml);

		menu.add(menuFormat);

		// processor
		JMenu menuProcessor = new JMenu("Process");
		
		// xml
		JMenuItem itemReCaptcha = new JMenuItem("ReCaptcha");
		itemReCaptcha.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onResponseProcessReCaptcha();
			}

		});
		menuProcessor.add(itemReCaptcha);

		menu.add(menuProcessor);

		this.add(menu);
	}

	protected void buildBookmark()
	{
		ArrayList<URL> bookmarks = config.getBookmarks();
		JMenu menu = new JMenu("Bookmark");

		for(int i = 0; i < bookmarks.size(); i++)
		{
			JMenuItem itemBookmark = new JMenuItem(bookmarks.get(i).toString());
			itemBookmark.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) 
				{
					JMenuItem item = (JMenuItem) e.getSource();

					listener.onBookmarkOpen(item.getText());
				}

			});
			menu.add(itemBookmark);
		}
		
		this.add(menu);
	}

	protected void buildView()
	{
		JMenu menu = new JMenu("View");

		// certificates
		JMenuItem itemCerts = new JMenuItem("Certificates");
		itemCerts.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onViewCertificates();
			}

		});
		menu.add(itemCerts);

		// cookies
		JMenuItem itemCookies = new JMenuItem("Cookies");
		itemCookies.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onViewCookies();
			}

		});
		menu.add(itemCookies);

		// log
		JMenuItem itemLog = new JMenuItem("Log");
		itemLog.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onViewLog();
			}

		});
		menu.add(itemLog);

		// about
		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onViewAbout();
			}

		});
		menu.add(itemAbout);

		this.add(menu);
	}

	public interface MenuBarActionListener
	{
		public void onUrlRun();
		public void onUrlReset();
		public void onUrlNewTab();
		public void onUrlCloseTab();
		public void onUrlSave();
		public void onUrlOpen();
		public void onBookmark();
		public void onBookmarkOpen(String url);
		public void onUrlFocus();
		public void onRequestBasicAuth();
		public void onRequestForm();
		public void onRequestOauthRequestToken();
		public void onRequestPingback();
		public void onRequestUpload();
		public void onResponseHtmlSearch();
		public void onResponseHtmlForm();
		public void onResponseHtmlImages();
		public void onResponseFormatHtml();
		public void onResponseFormatJson();
		public void onResponseFormatXml();
		public void onResponseProcessReCaptcha();
		public void onViewCertificates();
		public void onViewCookies();
		public void onViewLog();
		public void onViewAbout();
	}
}
