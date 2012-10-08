/**
 * $Id: FilterOut.java 27 2012-05-28 10:52:28Z k42b3.x@googlemail.com $
 * 
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

package com.k42b3.aletheia.filter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import com.k42b3.aletheia.Aletheia;

/**
 * FilterOut
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 * @version    $Revision: 27 $
 */
public class FilterOut extends JFrame
{
	private ArrayList<ConfigFilterAbstract> filtersConfig = new ArrayList<ConfigFilterAbstract>();
	private ArrayList<ResponseFilterAbstract> filters = new ArrayList<ResponseFilterAbstract>();
	
	private Aletheia aletheia;
	private ArrayList<ResponseFilterAbstract> activeFilters;

	private Logger logger = Logger.getLogger("com.k42b3.aletheia");

	public FilterOut(ArrayList<ResponseFilterAbstract> activeFilters)
	{
		this.activeFilters = activeFilters;


		// settings
		this.setTitle("Response filter");
		this.setLocation(100, 100);
		this.setSize(400, 300);
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());


		// tab panel
		JTabbedPane panel = new JTabbedPane();
		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));


		// add filters
		ArrayList<String> filterList = new ArrayList<String>();

		filterList.add("Location");


		// parse filters
		for(int i = 0; i < filterList.size(); i++)
		{
			try
			{
				String clsConfig = "com.k42b3.aletheia.filter.response." + filterList.get(i) + "Config";
				String cls = "com.k42b3.aletheia.filter.response." + filterList.get(i);

				Class cConfig = Class.forName(clsConfig);
				Class c = Class.forName(cls);

				ConfigFilterAbstract filterConfig = (ConfigFilterAbstract) cConfig.newInstance();
				ResponseFilterAbstract filter = (ResponseFilterAbstract) c.newInstance();

				// load config
				for(int j = 0; j < activeFilters.size(); j++)
				{
					if(activeFilters.get(j).getClass().getName().equals(filter.getClass().getName()))
					{
						filterConfig.onLoad(activeFilters.get(j).getConfig());
					}
				}

				this.filtersConfig.add(filterConfig);
				this.filters.add(filter);

				JScrollPane scpFilter = new JScrollPane(filterConfig);
				scpFilter.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
				scpFilter.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scpFilter.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

				panel.addTab(filterConfig.getName(), scpFilter);
			}
			catch(Exception e)
			{
				Aletheia.handleException(e);
			}
		}

		this.add(panel, BorderLayout.CENTER);


		// buttons
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout(FlowLayout.LEADING));

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new HandlerSave());
		panelButtons.add(btnSave);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new HandlerCancel());
		panelButtons.add(btnCancel);

		this.add(panelButtons, BorderLayout.SOUTH);
	}

	public void close()
	{
		this.setVisible(false);
	}

	private class HandlerSave implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			activeFilters.clear();

			for(int i = 0; i < filtersConfig.size(); i++)
			{
				if(filtersConfig.get(i).isActive())
				{
					filters.get(i).setConfig(filtersConfig.get(i).onSave());
					
					activeFilters.add(filters.get(i));
				}
			}

			close();
		}
	}
	
	private class HandlerCancel implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			close();
		}
	}
}
