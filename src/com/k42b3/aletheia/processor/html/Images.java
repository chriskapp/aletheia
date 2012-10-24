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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.Parser;
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

		executorService = Executors.newFixedThreadPool(8);
		
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
		this.setVisible(true);
	}

	private void close()
	{
		this.setVisible(false);
	}

	private void reset()
	{
		this.model.clear();
		this.images.clear();

		this.imagesLoaded = 0;
		this.imageCache.clear();
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
		for(int i = 0; i < html.length(); i++)
		{
			if(Parser.startsWith("<img", i, html))
			{
				String inputTag = Parser.getTag(i, html);
				String src = Parser.getAttribute("src", inputTag);

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

	private byte[] requestImage(URL imageUrl)
	{
		byte[] image = null;
		DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

		try
		{
			// http settings
			HttpParams params = new SyncBasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "UTF-8");
			HttpProtocolParams.setUserAgent(params, "Aletheia " + Aletheia.VERSION);
			HttpProtocolParams.setUseExpectContinue(params, true);
			
			HttpRequestInterceptor[] interceptors = {
				// Required protocol interceptors
				new RequestContent(),
				new RequestTargetHost(),
				// Recommended protocol interceptors
				new RequestConnControl(),
				new RequestUserAgent(),
				new RequestExpectContinue()
			};

			HttpProcessor httpproc = new ImmutableHttpProcessor(interceptors);
			HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
			
			// request settings
			int port = imageUrl.getPort();
			HttpContext context = new BasicHttpContext(null);
			HttpHost host = new HttpHost(imageUrl.getHost(), port == -1 ? 80 : port);

			context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
			context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);
			
			Socket socket = new Socket(host.getHostName(), host.getPort());
			conn.bind(socket, params);
			
			// build request
			BasicHttpRequest request = new BasicHttpRequest("GET", imageUrl.getPath());

			// request
			request.setParams(params);
			httpexecutor.preProcess(request, httpproc, context);

			HttpResponse response = httpexecutor.execute(request, conn, context);
			response.setParams(params);
			httpexecutor.postProcess(response, httpproc, context);

			image = EntityUtils.toByteArray(response.getEntity());

			// info
			imagesLoaded++;
			lblInfo.setText("Loading images (" + (imagesLoaded) + " / " + images.size() + ")");
		}
		catch(Exception e)
		{
			Aletheia.handleException(e);
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch(Exception e)
			{
				Aletheia.handleException(e);
			}
		}

		return image;
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
				BufferedImage im = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D g = im.createGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, 128, 128);

				label = new ImageLabel(imageUrl);
				ImageIcon icon = new ImageIcon(im);

				label.setIcon(icon);
				label.setText("Loading ...");
				label.setBorder(new EmptyBorder(4, 4, 4, 4));

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

	private class ImageLabel extends JLabel
	{
		private URL imageUrl;
		private ImageIcon icon;

		private int width;
		private int height;
		private String fileName;

		public ImageLabel(URL imageUrl)
		{
			this.imageUrl = imageUrl;

			this.setOpaque(true);
			this.load();
		}

		private void imageLoaded()
		{
			// set icon and text
			SwingUtilities.invokeLater(new Runnable() {

				public void run()
				{
					setIcon(icon);
					setText("<html><body><table><tr><td>Url:</td><td nowrap>" + imageUrl.toString() + "</td></tr><tr><td>Width:</td><td>" + width + "px</td></tr><tr><td>Height:</td><td>" + height + "px</td></tr><tr><td>File:</td><td>" + fileName + "</td></tr></table></body></html>");

					list.repaint();
				}

			});
		}

		private void load()
		{
			executorService.execute(new Runnable(){

				public void run()
				{
					BufferedImage image = null;

					try
					{
						byte[] rawImage = requestImage(imageUrl);

						if(rawImage != null)
						{
							InputStream bais = new ByteArrayInputStream(rawImage);
							image = ImageIO.read(bais);

							// noramlize image size
							if(image != null)
							{
								width = image.getWidth();
								height = image.getHeight();
								fileName = getFileName(imageUrl);

								BufferedImage im = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
								Graphics2D g = im.createGraphics();
								g.setBackground(Color.BLACK);
								g.fillRect(0, 0, 128, 128);
								g.drawImage(image, 0, 0, width, height, null);

								// set icon
								icon = new ImageIcon(im);

								// call image loaded
								imageLoaded();
							}
						}
					}
					catch(IOException e)
					{
						Aletheia.handleException(e);
					}
				}

			});
		}
	}
}

