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
import java.security.cert.X509Certificate;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.CertificateStore;
import com.k42b3.aletheia.protocol.Response;

/**
 * Form
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Certificates extends JFrame implements ProcessorInterface
{
	private CertTableModel model;
	private JTable table;
	private JButton btnDownload;

	private URL activeUrl;

	public Certificates()
	{
		super();

		// settings
		this.setTitle("Certificates");
		this.setLocation(100, 100);
		this.setPreferredSize(new Dimension(360, 200));
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());

		// table
		model = new CertTableModel();
		table = new JTable(model);
		table.setRowHeight(24);
		table.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e)
			{
			}

			public void mousePressed(MouseEvent e)
			{
				int row = table.getSelectedRow();

				btnDownload.setEnabled(row != -1);
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

		btnDownload = new JButton("Download");
		btnDownload.addActionListener(new DownloadHandler());

		panelButtons.add(btnDownload);

		this.add(panelButtons, BorderLayout.SOUTH);


		this.pack();
	}

	public void process(Response response) throws Exception 
	{
		// disable download
		btnDownload.setEnabled(false);

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

	private class DownloadHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			int row = table.getSelectedRow();
			
			if(row != -1)
			{
				// get selected cert
				X509Certificate cert = CertificateStore.getInstance().getCertificates().get(row);

				// save file
				String fileName = activeUrl != null ? activeUrl.getHost() + ".der" : "certificate.der";

				JFileChooser fc = new JFileChooser();
				fc.setSelectedFile(new File(fileName));

				int returnVal = fc.showSaveDialog(Certificates.this);

				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();

					try
					{
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(cert.getEncoded());
						fos.flush();
						fos.close();

						JOptionPane.showMessageDialog(null, "Export successful", "Information", JOptionPane.INFORMATION_MESSAGE);
					}
					catch(Exception ex)
					{
						Aletheia.handleException(ex);
					}
				}
			}
		}
	}

	private class CertTableModel extends DefaultTableModel
	{
		private String[] columns = {"Issuer", "Subject", "Not Before", "Not After"};

		public CertTableModel()
		{
			this.reload();
		}

		public void reload()
		{
			List<X509Certificate> certs = CertificateStore.getInstance().getCertificates();

			this.setNumRows(0);

			for(int i = 0; i < certs.size(); i++)
			{
				Object[] row = {
					certs.get(i).getIssuerX500Principal().toString(),
					certs.get(i).getSubjectX500Principal().toString(),
					certs.get(i).getNotBefore().toString(),
					certs.get(i).getNotAfter().toString()
				};

				this.addRow(row);
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
