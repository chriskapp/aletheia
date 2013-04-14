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

	public MenuBar()
	{
		super();

		buildUrl();
		buildProcessor();
		buildSample();
		buildHelp();
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
	
	protected void buildProcessor()
	{
		JMenu menu = new JMenu("Processor");

		// html
		JMenu menuHtml = new JMenu("Html");

		// form
		JMenuItem itemForm = new JMenuItem("Form");
		itemForm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		itemForm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onProcessorHtmlForm();
			}

		});
		menuHtml.add(itemForm);

		// images
		JMenuItem itemImages = new JMenuItem("Images");
		itemImages.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		itemImages.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onProcessorHtmlImages();
			}

		});
		menuHtml.add(itemImages);

		menu.add(menuHtml);

		// format
		JMenu menuFormat = new JMenu("Format");

		// xml
		JMenuItem itemXml = new JMenuItem("XML");
		//itemXml.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		itemXml.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onProcessorFormatXml();
			}

		});
		menuFormat.add(itemXml);
		
		// json
		JMenuItem itemJson = new JMenuItem("JSON");
		itemJson.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK));
		itemJson.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onProcessorFormatJson();
			}

		});
		menuFormat.add(itemJson);

		menu.add(menuFormat);

		// certificates
		JMenuItem itemCerts = new JMenuItem("Certificates");
		itemCerts.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onProcessorCertificates();
			}

		});
		menu.add(itemCerts);

		// cookies
		JMenuItem itemCookies = new JMenuItem("Cookies");
		itemCookies.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onProcessorCookies();
			}

		});
		menu.add(itemCookies);

		this.add(menu);
	}
	
	protected void buildSample()
	{
		JMenu menu = new JMenu("Sample");

		JMenuItem itemForm = new JMenuItem("Form");
		itemForm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onSampleForm();
			}

		});
		menu.add(itemForm);

		JMenuItem itemUpload = new JMenuItem("File Upload");
		itemUpload.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onSampleUpload();
			}

		});
		menu.add(itemUpload);

		this.add(menu);
	}

	protected void buildHelp()
	{
		JMenu menu = new JMenu("Help");

		JMenuItem itemLog = new JMenuItem("Log");
		itemLog.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onHelpLog();
			}

		});
		menu.add(itemLog);

		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				listener.onHelpAbout();
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
		public void onUrlFocus();
		public void onProcessorHtmlForm();
		public void onProcessorHtmlImages();
		public void onProcessorFormatXml();
		public void onProcessorFormatJson();
		public void onProcessorCertificates();
		public void onProcessorCookies();
		public void onSampleForm();
		public void onSampleUpload();
		public void onSampleWsHandshake();
		public void onHelpLog();
		public void onHelpAbout();
	}
}
