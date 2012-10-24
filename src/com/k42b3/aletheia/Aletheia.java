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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.k42b3.aletheia.filter.FilterIn;
import com.k42b3.aletheia.filter.FilterOut;
import com.k42b3.aletheia.filter.RequestFilterAbstract;
import com.k42b3.aletheia.filter.ResponseFilterAbstract;
import com.k42b3.aletheia.processor.ProcessorFactory;
import com.k42b3.aletheia.protocol.CallbackInterface;
import com.k42b3.aletheia.protocol.ProtocolFactory;
import com.k42b3.aletheia.protocol.ProtocolInterface;
import com.k42b3.aletheia.protocol.Request;
import com.k42b3.aletheia.protocol.Response;

/**
 * Aletheia
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Aletheia extends JFrame
{
	public static final String VERSION = "0.1.2 beta";

	public static Aletheia instance;

	private Config config;
	private History history;
	private JTabbedPane tp;

	private HashMap<Integer, ArrayList<RequestFilterAbstract>> filtersIn;
	private HashMap<Integer, ArrayList<ResponseFilterAbstract>> filtersOut;

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private Log logWin;
	private Logger logger = Logger.getLogger("com.k42b3.aletheia");

	private Aletheia()
	{
		// settings
		this.setTitle("Aletheia " + VERSION);
		this.setLocation(100, 100);
		this.setSize(800, 500);
		this.setMinimumSize(this.getSize());
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// load config
		config = new Config(Aletheia.getConfigFile());

		// history
		history = new History();

		// filter
		filtersIn = new HashMap<Integer, ArrayList<RequestFilterAbstract>>();
		filtersOut = new HashMap<Integer, ArrayList<ResponseFilterAbstract>>();

		// logging handler
		logger.addHandler(new Handler(){

			public void close() throws SecurityException 
			{
			}

			public void flush() 
			{
			}

			public void publish(LogRecord rec)
			{
				logWin.append(rec);
			}

		});

		logWin = new Log();
		logWin.pack();

		// menu
		this.setJMenuBar(this.buildMenuBar());

		// tabbed pane
		this.tp = new JTabbedPane();

		this.add(this.tp, BorderLayout.CENTER);
		
		// set stream handler
		URL.setURLStreamHandlerFactory(new DefaultURLStreamHandlerFactory());

		// add new tab
		this.newTab();
	}

	public void display()
	{
		this.pack();
		this.setVisible(true);
	}

	public void run(String url)
	{
		if(url.indexOf("://") == -1)
		{
			url = "http://" + url;
		}

		logger.info("Request " + url);

		try
		{
			// check whether valid url
			URL currentUrl = new URL(url);

			// set url
			this.getActiveUrl().setText(currentUrl.toString());

			// add to history
			history.add(currentUrl.toString());

			// get protocol
			ProtocolInterface protocol = ProtocolFactory.factory(currentUrl.getProtocol());

			// build request
			Request request = protocol.buildRequest(currentUrl, getActiveIn().getText());

			// set request
			protocol.setRequest(request, new CallbackInterface() {

				public void onResponse(Request request, Response response) 
				{
					// update request and response
					getActiveIn().setRequest(request);
					getActiveOut().setResponse(response);

					// apply response filter 
					if(filtersOut.containsKey(getSelectedIndex()))
					{
						ArrayList<ResponseFilterAbstract> filters = filtersOut.get(getSelectedIndex());

						for(int i = 0; i < filters.size(); i++)
						{
							try
							{
								filters.get(i).exec(response);
							}
							catch(Exception e)
							{
								Aletheia.handleException(e);
							}
						}
					}

					// update links
					getActiveSidebar().update(response);
				}

			});

			// apply request filter 
			if(this.filtersIn.containsKey(this.getSelectedIndex()))
			{
				ArrayList<RequestFilterAbstract> filters = this.filtersIn.get(this.getSelectedIndex());

				for(int i = 0; i < filters.size(); i++)
				{
					try
					{
						filters.get(i).exec(request);
					}
					catch(Exception e)
					{
						Aletheia.handleException(e);
					}
				}
			}

			// reset text fields
			this.getActiveIn().setRequest(request);
			this.getActiveOut().setText("");

			// start thread
			executor.execute(protocol);
		}
		catch(Exception e)
		{
			getActiveOut().setText(e.getMessage());

			Aletheia.handleException(e);
		}
	}

	public void reset()
	{
		getActiveUrl().setText("");
		getActiveIn().setText("");
		getActiveOut().setText("");
		getActiveSidebar().setVisible(false);

		SwingUtilities.invokeLater(new Runnable() {

			public void run() 
			{
				getActiveUrl().requestFocusInWindow();
			}

		});
	}

	public void newTab(boolean active)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		// url
		JPanel panelUrl = new JPanel();
		panelUrl.setBorder(BorderFactory.createEmptyBorder(2, 2, 4, 2));
		panelUrl.setLayout(new BorderLayout());

		TextFieldUrl url = new TextFieldUrl();
		url.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) 
			{
			}

			public void keyReleased(KeyEvent e) 
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					run(getActiveUrl().getText());
				}
				else if(e.getKeyCode() == KeyEvent.VK_UP)
				{
					if(history.hasPrevious())
					{
						getActiveUrl().setText(history.previous());
					}
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					if(history.hasNext())
					{
						getActiveUrl().setText(history.next());
					}
				}
			}

			public void keyPressed(KeyEvent e) 
			{
			}

		});

		panelUrl.add(url, BorderLayout.CENTER);

		panel.add(panelUrl, BorderLayout.NORTH);

		// main panel
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));


		// in
		JPanel panelIn = new JPanel();
		panelIn.setLayout(new BorderLayout());	
		panelIn.setPreferredSize(new Dimension(600, 180));

		// header
		JPanel panelInHeader = new JPanel();
		panelInHeader.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		panelInHeader.setLayout(new BorderLayout());

		JLabel lblIn = new JLabel("Request:");
		panelInHeader.add(lblIn, BorderLayout.CENTER);

		JPanel panelBtnFilterIn = new JPanel();
		panelBtnFilterIn.setLayout(new FlowLayout());

		JButton btnInFilter = new JButton("Filter");
		btnInFilter.addActionListener(new InFilterHandler());

		panelBtnFilterIn.add(btnInFilter);
		panelInHeader.add(panelBtnFilterIn, BorderLayout.EAST);

		panelIn.add(panelInHeader, BorderLayout.NORTH);

		// in textarea
		TextPaneIn in = new TextPaneIn();
		
		JScrollPane scrIn = new JScrollPane(in);
		scrIn.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
		scrIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		panelIn.add(scrIn, BorderLayout.CENTER);

		// out
		JPanel panelOut = new JPanel();
		panelOut.setLayout(new BorderLayout());

		// header
		JPanel panelOutHeader = new JPanel();
		panelOutHeader.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		panelOutHeader.setLayout(new BorderLayout());

		JLabel lblOut = new JLabel("Response:");
		panelOutHeader.add(lblOut, BorderLayout.CENTER);

		JPanel panelBtnFilterOut = new JPanel();
		panelBtnFilterOut.setLayout(new FlowLayout());

		JButton btnOutDownload = new JButton("Download");
		btnOutDownload.addActionListener(new OutDownloadHandler());

		JButton btnOutFilter = new JButton("Filter");
		btnOutFilter.addActionListener(new OutFilterHandler());

		panelBtnFilterOut.add(btnOutDownload);
		panelBtnFilterOut.add(btnOutFilter);
		panelOutHeader.add(panelBtnFilterOut, BorderLayout.EAST);

		panelOut.add(panelOutHeader, BorderLayout.NORTH);

		// out textarea
		TextPaneOut out = new TextPaneOut();

		JScrollPane scrOut = new JScrollPane(out);
		scrOut.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
		scrOut.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrOut.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		panelOut.add(scrOut, BorderLayout.CENTER);

		sp.add(panelIn);
		sp.add(panelOut);

		panel.add(sp, BorderLayout.CENTER);

		// sidebar
		Sidebar sb = new Sidebar();

		panel.add(sb, BorderLayout.EAST);

		this.tp.addTab("Request #" + this.tp.getTabCount(), panel);

		if(active)
		{
			this.tp.setSelectedIndex(this.tp.getTabCount() - 1);

			reset();
		}

		// load default filters
		int selectedIndex = this.tp.getTabCount() - 1;

		filtersIn.put(selectedIndex, config.getFiltersIn());
		filtersOut.put(selectedIndex, config.getFiltersOut());
	}

	public void newTab()
	{
		newTab(true);
	}

	public void closeTab()
	{
		if(this.tp.getTabCount() > 1)
		{
			this.tp.remove(this.tp.getSelectedIndex());
		}
	}

	public void save(File file)
	{
		try
		{
			// check file extension
			if(!file.getName().endsWith(".xml"))
			{
				file = new File(file.getAbsolutePath() + ".xml");
			}

			// build xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			doc.normalizeDocument();
			
			Element root = doc.createElement("aletheia");

			Element uri = doc.createElement("uri");
			uri.setTextContent(this.getActiveUrl().getText());

			Element request = doc.createElement("request");
			request.appendChild(doc.createCDATASection(this.getActiveIn().getText()));

			Element filters = doc.createElement("filters");

			// in filters
			if(this.filtersIn.containsKey(this.getSelectedIndex()))
			{
				Element in = doc.createElement("in");

				ArrayList<RequestFilterAbstract> filtersIn = this.filtersIn.get(this.getSelectedIndex());
				
				for(int i = 0; i < filtersIn.size(); i++)
				{
					Element filter = doc.createElement("filter");
					filter.setAttribute("name", filtersIn.get(i).getName());

					Properties config = filtersIn.get(i).getConfig();

					if(config != null)
					{
						Set set = config.entrySet();
						Iterator iter = set.iterator();

						while(iter.hasNext())
						{
							Map.Entry me = (Map.Entry) iter.next();

							Element property = doc.createElement("property");
							property.setAttribute("name", me.getKey().toString());
							property.setTextContent(me.getValue().toString());

							filter.appendChild(property);
						}
					}

					in.appendChild(filter);
				}

				filters.appendChild(in);
			}

			// out filters
			if(this.filtersOut.containsKey(this.getSelectedIndex()))
			{
				Element out = doc.createElement("out");

				ArrayList<ResponseFilterAbstract> filtersIn = this.filtersOut.get(this.getSelectedIndex());

				for(int i = 0; i < filtersIn.size(); i++)
				{
					Element filter = doc.createElement("filter");
					filter.setAttribute("name", filtersIn.get(i).getName());

					Properties config = filtersIn.get(i).getConfig();

					if(config != null)
					{
						Set set = config.entrySet();
						Iterator iter = set.iterator();

						while(iter.hasNext())
						{
							Map.Entry me = (Map.Entry) iter.next();

							Element property = doc.createElement("property");
							property.setAttribute("name", me.getKey().toString());
							property.setTextContent(me.getValue().toString());

							filter.appendChild(property);
						}
					}

					out.appendChild(filter);
				}

				filters.appendChild(out);
			}

			// append elements
			root.appendChild(uri);
			root.appendChild(request);
			root.appendChild(filters);

			doc.appendChild(root);

			// write to file
			Source source = new DOMSource(doc);
			Result result = new StreamResult(file);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);

			logger.info("Saved successful to " + file.getAbsolutePath());
		}
		catch(Exception e)
		{
			Aletheia.handleException(e);
		}
	}
	
	public void open(File file)
	{
		try
		{
			// read xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			Element rootElement = (Element) doc.getDocumentElement();

			rootElement.normalize();

			NodeList uriList = doc.getElementsByTagName("uri");
			NodeList requestList = doc.getElementsByTagName("request");

			if(uriList.getLength() > 0 && requestList.getLength() > 0)
			{
				getActiveUrl().setText(uriList.item(0).getTextContent());
				getActiveIn().setText(requestList.item(0).getTextContent());
			}
			else
			{
				throw new Exception("Uri or request element not found");
			}

			logger.info("Loaded successful from " + file.getAbsolutePath());
		}
		catch(Exception e)
		{
			Aletheia.handleException(e);
		}
	}

	/**
	 * Calls an processor wich in some way interpret or modify the response
	 * 
	 * @param String name
	 */
	public void callProcessor(String name)
	{
		try
		{
			ProcessorFactory.factory(name).process(getActiveOut().getResponse());
		}
		catch(Exception e)
		{
			Aletheia.handleException(e);
		}
	}

	public void log()
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run()
			{
				if(!logWin.isVisible())
				{
					logWin.setVisible(true);
				}

				logWin.requestFocus();
			}

		});
	}

	public void about()
	{
		StringBuilder out = new StringBuilder();

		out.append("Version: Aletheia " + VERSION + "\n");
		out.append("Author: Christoph \"k42b3\" Kappestein" + "\n");
		out.append("Website: http://aletheia.k42b3.com" + "\n");
		out.append("License: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
		out.append("\n");
		out.append("A browser like application to send raw http requests. It is designed for" + "\n");
		out.append("debugging and finding security issues in web applications. For the current" + "\n");
		out.append("version and more informations visit <http://code.google.com/p/aletheia>." + "\n");

		JOptionPane.showMessageDialog(this, out, "About", JOptionPane.INFORMATION_MESSAGE);
	}

	public Config getConfig()
	{
		return config;
	}

	/**
	 * Returns the url textfield of the current selected tab
	 * 
	 * @return TextFieldUrl
	 */
	public TextFieldUrl getActiveUrl()
	{
		JPanel mp = (JPanel) this.tp.getSelectedComponent();
		JPanel pa = (JPanel) mp.getComponent(0);
		TextFieldUrl url = (TextFieldUrl) pa.getComponent(0);

		return url;
	}

	/**
	 * Returns the request textpane of the current selected tab
	 * 
	 * @return TextPaneIn
	 */
	public TextPaneIn getActiveIn()
	{
		JPanel mp = (JPanel) this.tp.getSelectedComponent();
		JSplitPane sp = (JSplitPane) mp.getComponent(1);
		JPanel pa = (JPanel) sp.getComponent(1);
		JScrollPane cp = (JScrollPane) pa.getComponent(1);
		JViewport vp = (JViewport) cp.getComponent(0);
		TextPaneIn in = (TextPaneIn) vp.getComponent(0);

		return in;
	}

	/**
	 * Returns the response textpane of the current selected tab
	 * 
	 * @return TextPaneOut
	 */
	public TextPaneOut getActiveOut()
	{
		JPanel mp = (JPanel) this.tp.getSelectedComponent();
		JSplitPane sp = (JSplitPane) mp.getComponent(1);
		JPanel pa = (JPanel) sp.getComponent(2);
		JScrollPane cp = (JScrollPane) pa.getComponent(1);
		JViewport vp = (JViewport) cp.getComponent(0);
		TextPaneOut out = (TextPaneOut) vp.getComponent(0);

		return out;
	}

	/**
	 * Returns the sidebar of the current selected tab
	 * 
	 * @return Sidebar
	 */
	public Sidebar getActiveSidebar()
	{
		JPanel mp = (JPanel) this.tp.getSelectedComponent();
		Sidebar sb = (Sidebar) mp.getComponent(2);

		return sb;
	}

	/**
	 * Returns the current selected tab index
	 * 
	 * @return Integer
	 */
	private int getSelectedIndex()
	{
		return this.tp.getSelectedIndex();
	}

	/**
	 * Builds the main menu bar
	 *  
	 * @return JMenuBar
	 */
	private JMenuBar buildMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();


		// url
		JMenu menuUrl = new JMenu("URL");

		JMenuItem itemRun = new JMenuItem("Run");
		itemRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		itemRun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				run(getActiveUrl().getText());
			}

		});
		menuUrl.add(itemRun);

		JMenuItem itemReset = new JMenuItem("Reset");
		itemReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		itemReset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				reset();
			}

		});
		menuUrl.add(itemReset);

		JMenuItem itemNewTab = new JMenuItem("New Tab");
		itemNewTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		itemNewTab.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				newTab();
			}

		});
		menuUrl.add(itemNewTab);

		JMenuItem itemCloseTab = new JMenuItem("Close Tab");
		itemCloseTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		itemCloseTab.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				closeTab();
			}

		});
		menuUrl.add(itemCloseTab);

		JMenuItem itemSave = new JMenuItem("Save");
		itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		itemSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				// save file
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new XmlFilter());

				int returnVal = fc.showSaveDialog(Aletheia.this);

				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					save(fc.getSelectedFile());
				}
			}

		});
		menuUrl.add(itemSave);

		JMenuItem itemLoad = new JMenuItem("Open");
		itemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		itemLoad.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				// load file
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new XmlFilter());

				int returnVal = fc.showOpenDialog(Aletheia.this);

				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					open(fc.getSelectedFile());
				}
			}

		});
		menuUrl.add(itemLoad);

		JMenuItem itemFocus = new JMenuItem("Focus");
		itemFocus.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		itemFocus.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				if(!getActiveUrl().hasFocus())
				{
					getActiveUrl().requestFocus();
				}

				getActiveUrl().setSelectionStart(0);
				getActiveUrl().setSelectionEnd(getActiveUrl().getText().length());
			}

		});
		menuUrl.add(itemFocus);

		menuBar.add(menuUrl);

		
		// processor
		JMenu menuProcessor = new JMenu("Processor");

		// html
		JMenu menuHtml = new JMenu("Html");

		// form
		JMenuItem itemForm = new JMenuItem("Form");
		itemForm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		itemForm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				callProcessor("html.form");
			}

		});
		menuHtml.add(itemForm);

		// images
		JMenuItem itemImages = new JMenuItem("Images");
		itemImages.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		itemImages.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				callProcessor("html.images");
			}

		});
		menuHtml.add(itemImages);

		menuProcessor.add(menuHtml);

		// format
		JMenu menuFormat = new JMenu("Format");

		// xml
		JMenuItem itemXml = new JMenuItem("XML");
		//itemXml.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		itemXml.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				callProcessor("format.xml");
			}

		});
		menuFormat.add(itemXml);
		
		/*
		// json
		JMenuItem itemJson = new JMenuItem("JSON");
		itemJson.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK));
		itemJson.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				callProcessor("format.json");
			}

		});
		menuFormat.add(itemJson);
		*/

		menuProcessor.add(menuFormat);

		// certificates
		JMenuItem itemCerts = new JMenuItem("Certificates");
		itemCerts.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				callProcessor("certificates");
			}

		});
		menuProcessor.add(itemCerts);

		// cookies
		JMenuItem itemCookies = new JMenuItem("Cookies");
		itemCookies.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				callProcessor("cookies");
			}

		});
		menuProcessor.add(itemCookies);

		menuBar.add(menuProcessor);


		// help
		JMenu menuHelp = new JMenu("Help");

		JMenuItem itemLog = new JMenuItem("Log");
		itemLog.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				log();
			}

		});
		menuHelp.add(itemLog);

		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				about();
			}

		});
		menuHelp.add(itemAbout);

		menuBar.add(menuHelp);

		return menuBar;
	}

	/**
	 * A common method to handle exceptions
	 * 
	 * @param Exception e
	 */
	public static void handleException(Exception e)
	{
		Logger.getLogger("com.k42b3.aletheia").log(Level.WARNING, e.getMessage());

		e.printStackTrace();
	}

	/**
	 * Returns the config file
	 * 
	 * @return File
	 */
	public static File getConfigFile()
	{
		return new File("aletheia.conf.xml");
	}

	/**
	 * Returns the instance of aletheia
	 * 
	 * @return Aletheia
	 */
	public static Aletheia getInstance()
	{
		if(instance == null)
		{
			instance = new Aletheia();
		}

		return instance;
	}

	public class InFilterHandler implements ActionListener
	{
		private FilterIn filterWin;

		public void actionPerformed(ActionEvent e) 
		{
			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					if(filterWin == null)
					{
						if(!filtersIn.containsKey(getSelectedIndex()))
						{
							filtersIn.put(getSelectedIndex(), new ArrayList<RequestFilterAbstract>());
						}

						filterWin = new FilterIn(filtersIn.get(getSelectedIndex()));
					}

					filterWin.pack();
					filterWin.setVisible(true);		
				}

			});
		}
	}

	public class OutFilterHandler implements ActionListener
	{
		private FilterOut filterWin;
		
		public void actionPerformed(ActionEvent e) 
		{
			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					if(filterWin == null)
					{
						if(!filtersOut.containsKey(getSelectedIndex()))
						{
							filtersOut.put(getSelectedIndex(), new ArrayList<ResponseFilterAbstract>());
						}

						filterWin = new FilterOut(filtersOut.get(getSelectedIndex()));
					}

					filterWin.pack();
					filterWin.setVisible(true);			
				}

			});
		}
	}

	public class OutDownloadHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			// save file
			JFileChooser fc = new JFileChooser();

			int returnVal = fc.showSaveDialog(Aletheia.this);

			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();

				try
				{
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(getActiveOut().getResponse().getContent());
					fos.flush();
					fos.close();

					JOptionPane.showMessageDialog(null, "Download successful", "Information", JOptionPane.INFORMATION_MESSAGE);
				}
				catch(Exception ex)
				{
					handleException(ex);
				}
			}
		}
	}

	public class XmlFilter extends FileFilter
	{
		public boolean accept(File file)
		{
			if(file.isFile())
			{
				int pos = file.getName().lastIndexOf('.');

				if(pos != -1 && file.getName().length() > pos + 1)
				{
					return file.getName().substring(pos + 1).toLowerCase().equals("xml");
				}
			}

			return false;
	    }

		public String getDescription() 
		{
			return "XML File";
		}
	}
	
	public class DefaultURLStreamHandlerFactory implements URLStreamHandlerFactory
	{
		public URLStreamHandler createURLStreamHandler(String protocol) 
		{
			try
			{
				return ProtocolFactory.factory(protocol).getStreamHandler();
			}
			catch(Exception e)
			{
			}

			return null;
		}
	}
}
