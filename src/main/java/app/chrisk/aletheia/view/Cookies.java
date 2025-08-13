/**
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

package app.chrisk.aletheia.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import app.chrisk.aletheia.Cookie;
import app.chrisk.aletheia.CookieStore;

/**
 * Form
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Cookies extends JFrame
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
	}

	public void load(URL url) throws Exception 
	{
		// disable download
		btnRemove.setEnabled(false);

		// set active url
		activeUrl = url;

		// reload
		model.reload();
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
