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

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;

/**
 * MenuBar
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
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
		itemRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		itemRun.addActionListener(e -> listener.onUrlRun());
		menu.add(itemRun);

		JMenuItem itemReset = new JMenuItem("Reset");
		itemReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		itemReset.addActionListener(e -> listener.onUrlReset());
		menu.add(itemReset);

		JMenuItem itemNewTab = new JMenuItem("New Tab");
		itemNewTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
		itemNewTab.addActionListener(e -> listener.onUrlNewTab());
		menu.add(itemNewTab);

		JMenuItem itemCloseTab = new JMenuItem("Close Tab");
		itemCloseTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
		itemCloseTab.addActionListener(e -> listener.onUrlCloseTab());
		menu.add(itemCloseTab);

		JMenuItem itemSave = new JMenuItem("Save");
		itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		itemSave.addActionListener(e -> listener.onUrlSave());
		menu.add(itemSave);

		JMenuItem itemLoad = new JMenuItem("Open");
		itemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		itemLoad.addActionListener(e -> listener.onUrlOpen());
		menu.add(itemLoad);

		JMenuItem itemBookmark = new JMenuItem("Bookmark");
		itemBookmark.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
		itemBookmark.addActionListener(e -> listener.onBookmark());
		menu.add(itemBookmark);
			
		JMenuItem itemFocus = new JMenuItem("Focus");
		itemFocus.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		itemFocus.addActionListener(e -> listener.onUrlFocus());
		menu.add(itemFocus);

		this.add(menu);
	}
	
	protected void buildRequest()
	{
		JMenu menu = new JMenu("Request");

		JMenuItem itemBasicAuth = new JMenuItem("Basic Auth");
		itemBasicAuth.addActionListener(e -> listener.onRequestBasicAuth());
		menu.add(itemBasicAuth);

		JMenuItem itemForm = new JMenuItem("Form");
		itemForm.addActionListener(e -> listener.onRequestForm());
		menu.add(itemForm);

		JMenuItem itemUpload = new JMenuItem("Upload");
		itemUpload.addActionListener(e -> listener.onRequestUpload());
		menu.add(itemUpload);

		JMenuItem itemSoap = new JMenuItem("SOAP");
		itemSoap.addActionListener(e -> listener.onRequestSoap());
		menu.add(itemSoap);

		this.add(menu);
	}

	protected void buildResponse()
	{
		JMenu menu = new JMenu("Response");

		// html
		JMenu menuHtml = new JMenu("HTML");

		// search
		JMenuItem itemSearch = new JMenuItem("Search");
		itemSearch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		itemSearch.addActionListener(e -> listener.onResponseHtmlSearch());
		menuHtml.add(itemSearch);
		
		// form
		JMenuItem itemForm = new JMenuItem("Form");
		itemForm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		itemForm.addActionListener(e -> listener.onResponseHtmlForm());
		menuHtml.add(itemForm);

		menu.add(menuHtml);

		// format
		JMenu menuFormat = new JMenu("Format");

		// html
		JMenuItem itemHtml = new JMenuItem("HTML");
		//itemJson.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		itemHtml.addActionListener(e -> listener.onResponseFormatHtml());
		menuFormat.add(itemHtml);
		
		// json
		JMenuItem itemJson = new JMenuItem("JSON");
		//itemJson.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK));
		itemJson.addActionListener(e -> listener.onResponseFormatJson());
		menuFormat.add(itemJson);

		// xml
		JMenuItem itemXml = new JMenuItem("XML");
		//itemXml.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		itemXml.addActionListener(e -> listener.onResponseFormatXml());
		menuFormat.add(itemXml);

		menu.add(menuFormat);

		this.add(menu);
	}

	protected void buildBookmark()
	{
		ArrayList<URL> bookmarks = config.getBookmarks();
		JMenu menu = new JMenu("Bookmark");

        for (URL bookmark : bookmarks) {
            JMenuItem itemBookmark = new JMenuItem(bookmark.toString());
            itemBookmark.addActionListener(e -> {
                JMenuItem item = (JMenuItem) e.getSource();

                listener.onBookmarkOpen(item.getText());
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
		itemCerts.addActionListener(e -> listener.onViewCertificates());
		menu.add(itemCerts);

		// cookies
		JMenuItem itemCookies = new JMenuItem("Cookies");
		itemCookies.addActionListener(e -> listener.onViewCookies());
		menu.add(itemCookies);

		// log
		JMenuItem itemLog = new JMenuItem("Log");
		itemLog.addActionListener(e -> listener.onViewLog());
		menu.add(itemLog);

		// about
		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(e -> listener.onViewAbout());
		menu.add(itemAbout);

		this.add(menu);
	}

	public interface MenuBarActionListener
	{
		void onUrlRun();
		void onUrlReset();
		void onUrlNewTab();
		void onUrlCloseTab();
		void onUrlSave();
		void onUrlOpen();
		void onBookmark();
		void onBookmarkOpen(String url);
		void onUrlFocus();
		void onRequestBasicAuth();
		void onRequestForm();
		void onRequestUpload();
		void onRequestSoap();
		void onResponseHtmlSearch();
		void onResponseHtmlForm();
		void onResponseFormatHtml();
		void onResponseFormatJson();
		void onResponseFormatXml();
		void onViewCertificates();
		void onViewCookies();
		void onViewLog();
		void onViewAbout();
	}
}
