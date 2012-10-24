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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.Parser;
import com.k42b3.aletheia.processor.ProcessorFactory;
import com.k42b3.aletheia.processor.ProcessorInterface;
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
public class Form extends JFrame implements ProcessorInterface
{
	private ArrayList<FormData> forms = new ArrayList<FormData>();
	private ArrayList<FormTableModel> fields = new ArrayList<FormTableModel>();
	private JTabbedPane tb;

	private String baseUrl;

	public Form()
	{
		super();

		// settings
		this.setTitle("Form");
		this.setLocation(100, 100);
		this.setSize(360, 400);
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());

		// tab panel
		tb = new JTabbedPane();

		this.add(tb, BorderLayout.CENTER);
		
		// buttons
		JPanel panelButtons = new JPanel();

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		panelButtons.setLayout(fl);

		JButton btnInsert = new JButton("Insert");
		btnInsert.setMnemonic(java.awt.event.KeyEvent.VK_I);
		btnInsert.addActionListener(new InsertHandler());

		panelButtons.add(btnInsert);

		this.add(panelButtons, BorderLayout.SOUTH);


		this.pack();
	}

	public void process(Response response) throws Exception 
	{
		// set visible
		this.setVisible(true);

		// get content
		String html = ProcessorFactory.getResponseContent(response);

		// proccess
		if(html != null)
		{
			this.reset();

			// set base url
			this.baseUrl = Aletheia.getInstance().getActiveUrl().getText();

			// parse form
			this.parseForms(html);

			// build components
			this.buildElements();
		}
	}

	private void reset()
	{
		this.forms.clear();
		this.fields.clear();
		this.tb.removeAll();
	}

	private void insert()
	{
		if(this.fields.size() > 0)
		{
			StringBuilder response = new StringBuilder();
			FormTableModel model = this.fields.get(this.tb.getSelectedIndex());
			
			for(int i = 0; i < model.getRowCount(); i++)
			{
				boolean active = (boolean) model.getValueAt(i, 0);
				
				if(active)
				{
					String key = "" + model.getValueAt(i, 1);
					String value = "" + model.getValueAt(i, 2);

					response.append(key + "=" + value);
					response.append("&");
				}
			}

			// append data depending on form method
			String method = forms.get(this.tb.getSelectedIndex()).getMethod();
			String actionUrl = forms.get(this.tb.getSelectedIndex()).getUrl();
			String activeUrl = Aletheia.getInstance().getActiveUrl().getText();

			// build url
			try
			{
				// set url
				String url = Util.resolveHref(activeUrl, actionUrl);

				// get path
				URL currentUrl = new URL(url);
				String path = currentUrl.getPath();

				if(currentUrl.getQuery() != null)
				{
					path = path + "?" + currentUrl.getQuery();
				}

				if(currentUrl.getRef() != null)
				{
					path = path + "#" + currentUrl.getRef();
				}

				// insert query
				Request request = (Request) Aletheia.getInstance().getActiveIn().getRequest();

				if(method.equals("GET"))
				{
					url = Util.appendQuery(url, response.toString());

					Aletheia.getInstance().getActiveUrl().setText(url);

					request.setLine(method, path);
				}
				else if(method.equals("POST"))
				{
					Aletheia.getInstance().getActiveUrl().setText(url);

					request.setLine(method, path);
					request.setHeader("Content-Type", "application/x-www-form-urlencoded");
					request.setBody(response.toString());
				}

				Aletheia.getInstance().getActiveIn().update();
			}
			catch(Exception e)
			{
				Aletheia.handleException(e);
			}
		}

		this.setVisible(false);
	}

	private void buildElements()
	{
		if(forms.size() > 0)
		{
			for(int i = 0; i < forms.size(); i++)
			{
				Set<Entry<String, String>> set = forms.get(i).getValues().entrySet();
				Iterator<Entry<String, String>> iter = set.iterator();

				FormTableModel formModel = new FormTableModel();
				JTable formTable = new JTable(formModel);
				formTable.setRowHeight(24);
				formTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				formTable.getColumnModel().getColumn(0).setPreferredWidth(40);
				formTable.getColumnModel().getColumn(1).setPreferredWidth(150);
				formTable.getColumnModel().getColumn(2).setPreferredWidth(150);

				while(iter.hasNext())
				{
					Map.Entry<String, String> item = (Map.Entry<String, String>) iter.next();

					Object[] row = {true, item.getKey(), item.getValue()};

					formModel.addRow(row);
				}

				JScrollPane scp = new JScrollPane(formTable);
				scp.setBorder(new EmptyBorder(4, 4, 4, 4));

				tb.addTab("Form #" + i, scp);

				this.fields.add(formModel);
			}
		}
		else
		{
			JPanel containerPanel = new JPanel();
			containerPanel.setLayout(new FlowLayout());
			containerPanel.add(new JLabel("No elements found"));

			tb.addTab("Form #0", containerPanel);
		}
	}

	private void parseForms(String html)
	{
		FormData form = null;

		for(int i = 0; i < html.length(); i++)
		{
			if(form == null)
			{
				if(Parser.startsWith("<form", i, html))
				{
					String formTag = Parser.getTag(i, html);
					String method = Parser.getAttribute("method", formTag);
					String action = Parser.getAttribute("action", formTag);

					form = new FormData(method, action);
				}
			}
			else
			{
				if(Parser.startsWith("<input", i, html))
				{
					String inputTag = Parser.getTag(i, html);
					String name = Parser.getAttribute("name", inputTag);
					String value = Parser.getAttribute("value", inputTag);

					form.addElement(name, value);
				}

				if(Parser.startsWith("<textarea", i, html))
				{
					String textareaTag = Parser.getTag(i, html);
					String name = Parser.getAttribute("name", textareaTag);

					form.addElement(name, "");
				}

				if(Parser.startsWith("<select", i, html))
				{
					String selectTag = Parser.getTag(i, html);
					String name = Parser.getAttribute("name", selectTag);

					form.addElement(name, "");
				}

				if(Parser.startsWith("</form>", i, html))
				{
					forms.add(form);

					form = null;
				}
			}
		}
	}

	private class FormData
	{
		private String url;
		private String method;
		private HashMap<String, String> values = new HashMap<String, String>();

		public FormData(String method, String url)
		{
			this.setMethod(method);
			this.setUrl(url);
		}

		public FormData(String method)
		{
			this(method, null);
		}

		public String getUrl() 
		{
			return url;
		}

		public void setUrl(String url) 
		{
			this.url = url;
		}

		public String getMethod() 
		{
			return method;
		}

		public void setMethod(String method) 
		{
			if(method == null || method.isEmpty())
			{
				method = "GET";
			}

			method = method.toUpperCase();

			if(method.equals("GET") || method.equals("POST"))
			{
				this.method = method;
			}
			else
			{
				this.method = "GET";
			}
		}

		public void addElement(String name, String value)
		{
			if(name != null && !name.isEmpty())
			{
				values.put(name, value);
			}
		}

		public HashMap<String, String> getValues() 
		{
			return values;
		}
	}

	private class InsertHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			insert();
		}
	}
	
	private class FormTableModel extends DefaultTableModel
	{
		private String[] columns = {"", "Key", "Value"};

		public FormTableModel()
		{
		}

		public Class getColumnClass(int columnIndex)
		{
			return columnIndex == 0 ? Boolean.class : String.class;
		}

		public int getColumnCount()
		{
			return 3;
		}
		
		public String getColumnName(int column)
		{
			return column > 0 && column < this.columns.length ? this.columns[column] : null;
		}
		
		public boolean isCellEditable(int row, int column)
		{
			return true;
		}
	}
}
