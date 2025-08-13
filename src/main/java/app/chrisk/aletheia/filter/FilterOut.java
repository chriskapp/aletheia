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

package app.chrisk.aletheia.filter;

import app.chrisk.aletheia.Aletheia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * FilterOut
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class FilterOut extends JFrame
{
	private final ArrayList<ConfigFilterAbstract> filtersConfig = new ArrayList<>();
	private final ArrayList<ResponseFilterAbstract> filters = new ArrayList<>();
	
	private final ArrayList<ResponseFilterAbstract> activeFilters;

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
		ArrayList<String> filterList = new ArrayList<>();
		filterList.add("Cookie");
		filterList.add("Location");
		filterList.add("Application");
		filterList.add("Syntax");

		// parse filters
        for (String s : filterList) {
            try {
                String clsConfig = "app.chrisk.aletheia.filter.response." + s + "Config";
                String cls = "app.chrisk.aletheia.filter.response." + s;

                Class<?> classConfig = Class.forName(clsConfig);
                Class<?> classResponse = Class.forName(cls);

                ConfigFilterAbstract filterConfig = (ConfigFilterAbstract) classConfig.newInstance();
                ResponseFilterAbstract filter = (ResponseFilterAbstract) classResponse.newInstance();

                // load config
                for (ResponseFilterAbstract activeFilter : activeFilters) {
                    if (activeFilter.getClass().getName().equals(filter.getClass().getName())) {
                        filterConfig.onLoad(activeFilter.getConfig());
                    }
                }

                this.filtersConfig.add(filterConfig);
                this.filters.add(filter);

                JScrollPane scpFilter = new JScrollPane(filterConfig);
                scpFilter.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                scpFilter.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                scpFilter.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                panel.addTab(filterConfig.getName(), scpFilter);
            } catch (Exception e) {
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
