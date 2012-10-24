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

package com.k42b3.aletheia.processor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.codec.binary.Base64;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.CertificateStore;
import com.k42b3.aletheia.Cookie;
import com.k42b3.aletheia.CookieStore;
import com.k42b3.aletheia.Parser;
import com.k42b3.aletheia.processor.ProcessorFactory;
import com.k42b3.aletheia.processor.ProcessorInterface;
import com.k42b3.aletheia.processor.html.Images;
import com.k42b3.aletheia.protocol.Response;
import com.k42b3.aletheia.protocol.http.Request;
import com.k42b3.aletheia.protocol.http.Util;

/**
 * Form
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Cookies extends JFrame implements ProcessorInterface
{
	private CookieTableModel model;
	private JTable table;
	private JButton btnRemove;

	private URL activeUrl;

	public Cookies()
	{
		super();

		// settings
		this.setTitle("Cookies");
		this.setLocation(100, 100);
		this.setPreferredSize(new Dimension(360, 400));
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());

		// table
		model = new CookieTableModel();
		table = new JTable(model);
		table.setRowHeight(24);
		table.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e)
			{
			}

			public void mousePressed(MouseEvent e)
			{
				int row = table.getSelectedRow();

				btnRemove.setEnabled(row != -1);
			}

			public void mouseExited(MouseEvent e)
			{
			}

			public void mouseEntered(MouseEvent e)
			{
			}

			public void mouseClicked(MouseEvent e)
			{
			}

		});

		JScrollPane scp = new JScrollPane(table);
		scp.setBorder(new EmptyBorder(4, 4, 4, 4));

		this.add(scp, BorderLayout.CENTER);

		// buttons
		JPanel panelButtons = new JPanel();

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		panelButtons.setLayout(fl);

		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new RemoveHandler());

		panelButtons.add(btnRemove);

		this.add(panelButtons, BorderLayout.SOUTH);


		this.pack();
	}

	public void process(Response response) throws Exception 
	{
		// disable download
		btnRemove.setEnabled(false);

		// set active url
		try
		{
			activeUrl = new URL(Aletheia.getInstance().getActiveUrl().getText());
		}
		catch(MalformedURLException ex)
		{
		}

		// reload
		model.reload();

		// set visible
		this.setVisible(true);
	}

	private class RemoveHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			int row = table.getSelectedRow();

			if(row != -1)
			{
				String name = model.getValueAt(row, 0).toString();

				CookieStore.getInstance().deleteCookie(activeUrl.getHost(), new Cookie(name, null));

				// disable download
				btnRemove.setEnabled(false);

				// reload
				model.reload();
			}
		}
	}

	private class CookieTableModel extends DefaultTableModel
	{
		private String[] columns = {"Name", "Value"};

		public CookieTableModel()
		{
			this.reload();
		}

		public void reload()
		{
			if(activeUrl == null)
			{
				return;
			}

			LinkedList<Cookie> cookies = CookieStore.getInstance().getCookies(activeUrl.getHost());

			this.setNumRows(0);

			if(cookies != null)
			{
				for(int i = 0; i < cookies.size(); i++)
				{
					Object[] row = {
						cookies.get(i).getName(),
						cookies.get(i).getValue()
					};

					this.addRow(row);
				}
			}
		}

		public int getColumnCount()
		{
			return columns.length;
		}
		
		public String getColumnName(int column)
		{
			return column >= 0 && column < this.columns.length ? this.columns[column] : null;
		}

		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	}
}
