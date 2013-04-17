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

package com.k42b3.aletheia.processor.html;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.processor.ProcessorFactory;
import com.k42b3.aletheia.processor.ProcessorInterface;
import com.k42b3.aletheia.protocol.Response;
import com.k42b3.aletheia.protocol.http.Util;

/**
 * Images
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Images extends JFrame implements ProcessorInterface
{
	private HashMap<String, ImageLabel> imageCache = new HashMap<String, ImageLabel>();
	private ArrayList<URL> images = new ArrayList<URL>();

	private DefaultListModel<URL> model;
	private JList<URL> list;
	private JScrollPane scp;
	
	private JButton btnDownload;
	private JLabel lblInfo;

	private String baseUrl;
	private ExecutorService executorService;
	private int imagesLoaded = 0;

	public Images()
	{
		super();

		executorService = Executors.newFixedThreadPool(6);
		
		// settings
		this.setTitle("Images");
		this.setLocation(100, 100);
		this.setPreferredSize(new Dimension(360, 600));
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());

		// list
		model = new DefaultListModel<URL>();
		list = new JList<URL>(model);
		list.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e)
			{
				btnDownload.setEnabled(list.getSelectedIndex() != -1);
			}

		});
		list.setCellRenderer(new ImageCellRenderer());

		scp = new JScrollPane(list);
		scp.setBorder(new EmptyBorder(4, 4, 4, 4));

		this.add(scp, BorderLayout.CENTER);

		// buttons
		JPanel panelButtons = new JPanel();

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		panelButtons.setLayout(fl);

		btnDownload = new JButton("Download");
		btnDownload.addActionListener(new DownloadHandler());
		btnDownload.setEnabled(false);
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new CloseHandler());
		lblInfo = new JLabel("");

		panelButtons.add(btnDownload);
		panelButtons.add(btnCancel);
		panelButtons.add(lblInfo);

		this.add(panelButtons, BorderLayout.SOUTH);

		this.pack();
	}

	public void process(Response response) throws Exception 
	{
		// get content
		String html = ProcessorFactory.getResponseContent(response);

		// proccess
		if(html != null)
		{
			// reset
			this.reset();

			// set base url
			this.baseUrl = Aletheia.getInstance().getActiveUrl().getText();

			// parse form
			this.parseImages(html);

			// build components
			this.buildElements();
		}

		// set visible
		this.pack();
		this.setVisible(true);
	}

	private void close()
	{
		this.dispose();
		this.setVisible(false);
	}

	private void reset()
	{
		this.imagesLoaded = 0;
		this.imageCache.clear();
		this.images.clear();

		this.model.clear();
	}

	private void buildElements()
	{
		if(images.size() > 0)
		{
			for(int i = 0; i < images.size(); i++)
			{
				model.addElement(images.get(i));
			}
		}
	}

	private void parseImages(String html)
	{
		Document doc = Jsoup.parse(html);
		Elements image = doc.getElementsByTag("img");

		for(Element img : image)
		{
			String src = img.attr("src");

			if(!src.isEmpty())
			{
				try
				{
					URL url = new URL(Util.resolveHref(baseUrl, src));

					if(!images.contains(url))
					{
						images.add(url);
					}
				}
				catch(Exception e)
				{
					Aletheia.handleException(e);
				}
			}
		}
	}

	private byte[] requestImage(URL imageUrl, int count)
	{
		byte[] image = null;

		try
		{
			if(count > 4)
			{
				throw new Exception("Max redirection reached");
			}

			DefaultHttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpGet(imageUrl.toString());
			HttpResponse response = client.execute(request);

			// redirect
			Header location = response.getFirstHeader("Location");
			if(location != null)
			{
				URL url = new URL(location.getValue());

				if(!url.toString().equals(imageUrl.toString()))
				{
					return requestImage(url, count + 1);
				}
			}

			// read image
			image = EntityUtils.toByteArray(response.getEntity());

			// info
			imagesLoaded++;
			lblInfo.setText("Loading images (" + (imagesLoaded) + " / " + images.size() + ")");
		}
		catch(Exception e)
		{
			Aletheia.handleException(e);
		}

		return image;
	}

	private byte[] requestImage(URL imageUrl)
	{
		return requestImage(imageUrl, 0);
	}

	private String getFileName(URL url)
	{
		String path = url.getFile();
		int pos = path.lastIndexOf('/');

		if(pos != -1)
		{
			return path.substring(pos + 1);
		}
		else
		{
			return path;
		}
	}

	private class DownloadHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			URL imageUrl = list.getSelectedValue();

			if(imageUrl != null)
			{
				// download image
				byte[] image = requestImage(imageUrl);

				// save file
				JFileChooser fc = new JFileChooser();
				fc.setSelectedFile(new File(getFileName(imageUrl)));

				int returnVal = fc.showSaveDialog(Images.this);

				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();

					try
					{
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(image);
						fos.flush();
						fos.close();

						JOptionPane.showMessageDialog(null, "Download successful", "Information", JOptionPane.INFORMATION_MESSAGE);
					}
					catch(Exception ex)
					{
						Aletheia.handleException(ex);
					}
				}
			}
		}
	}

	private class CloseHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			close();
		}
	}

	private class ImageCellRenderer implements ListCellRenderer<Object> 
	{
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			URL imageUrl = (URL) value;
			ImageLabel label;

			if(!imageCache.containsKey(imageUrl.toString()))
			{
				label = new ImageLabel(imageUrl);

				imageCache.put(imageUrl.toString(), label);
			}
			else
			{
				label = imageCache.get(imageUrl.toString());
			}

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
	}

	private class ImageLabel extends Canvas
	{
		public final static int WIDTH = 330;
		public final static int HEIGHT = 128;

		private URL imageUrl;
		private Image image;

		public ImageLabel(URL imageUrl)
		{
			this.imageUrl = imageUrl;

			load();
		}

		public void paint(Graphics g)
		{
			g.setColor(getBackground());
			g.fillRect(0, 0, WIDTH, HEIGHT);

			if(image != null)
			{
				g.drawImage(image, 0, 0, null);
			}

			g.setColor(getForeground());
			g.drawString(imageUrl.toString(), 10, 20);
		}

		public Dimension getPreferredSize() 
		{
			return new Dimension(WIDTH, HEIGHT);
		}

		private void load()
		{
			executorService.execute(new Runnable() {

				public void run()
				{
					image = Toolkit.getDefaultToolkit().createImage(requestImage(imageUrl));

					Toolkit.getDefaultToolkit().prepareImage(image, WIDTH, HEIGHT, new ImageObserver() {

						public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
						{
							list.repaint();

							return false;
						}

					});
				}

			});
		}
	}
}

