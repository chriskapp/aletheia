package app.chrisk.aletheia.processor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * DefaultProcessProperties
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class DefaultProcessProperties extends ProcessPropertiesAbstract
{
	private Properties properties;
	private ProcessPropertiesCallback callback;

	private PropertiesTableModel model;
	private JTable table;

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
		table = new JTable(model);
		table.setRowHeight(24);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);

		Set<Entry<Object, Object>> set = properties.entrySet();
		Iterator<Entry<Object, Object>> iter = set.iterator();

		while(iter.hasNext())
		{
			Map.Entry<Object, Object> item = (Map.Entry<Object, Object>) iter.next();

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
		btnInsert.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				insert();
			}

		});

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setMnemonic(java.awt.event.KeyEvent.VK_C);
		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				close();
			}

		});

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

	private class PropertiesTableModel extends DefaultTableModel
	{
		private String[] columns = {"Key", "Value"};

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
