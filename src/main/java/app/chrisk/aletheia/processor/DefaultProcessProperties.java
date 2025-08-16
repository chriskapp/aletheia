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

package app.chrisk.aletheia.processor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * DefaultProcessProperties
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class DefaultProcessProperties extends ProcessPropertiesAbstract
{
	private final Properties properties;
	private ProcessPropertiesCallback callback;

	private final PropertiesTableModel model;

    public DefaultProcessProperties(Properties properties)
	{
		super();

		this.properties = properties;

		// settings
		this.setTitle("Properties");
		this.setLocation(100, 100);
		this.setPreferredSize(new Dimension(320, 400));
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());

		// table
		model = new PropertiesTableModel();
        JTable table = new JTable(model);
		table.setRowHeight(24);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);

		Set<Entry<Object, Object>> set = properties.entrySet();

        for (Entry<Object, Object> item : set) {
            Object[] row = {item.getKey(), item.getValue()};

            model.addRow(row);
        }

		JScrollPane scp = new JScrollPane(table);

		this.add(scp, BorderLayout.CENTER);

		// buttons
		JPanel panelButtons = new JPanel();

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		panelButtons.setLayout(fl);

		JButton btnInsert = new JButton("Insert");
		btnInsert.setMnemonic(java.awt.event.KeyEvent.VK_I);
		btnInsert.addActionListener(e -> insert());

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setMnemonic(java.awt.event.KeyEvent.VK_C);
		btnCancel.addActionListener(e -> close());

		panelButtons.add(btnInsert);
		panelButtons.add(btnCancel);

		this.add(panelButtons, BorderLayout.SOUTH);
	}

	public void setCallback(ProcessPropertiesCallback callback)
	{
		this.callback = callback;
	}

	private void insert()
	{
		// insert data
		for(int i = 0; i < model.getRowCount(); i++)
		{
			String key = model.getValueAt(i, 0) == null ? "" : model.getValueAt(i, 0).toString();
			String value = model.getValueAt(i, 1) == null ? "" : model.getValueAt(i, 1).toString();

			properties.setProperty(key, value);
		}

		if(callback != null)
		{
			callback.onSubmit(properties);
		}

		this.setVisible(false);
	}

	private void close()
	{
		if(callback != null)
		{
			callback.onCancel();
		}

		this.setVisible(false);
	}

	private static class PropertiesTableModel extends DefaultTableModel
	{
		private final String[] columns = {"Key", "Value"};

		public PropertiesTableModel()
		{
		}

		public Class getColumnClass(int columnIndex)
		{
			return String.class;
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
			return column == 1;
		}
	}
}
