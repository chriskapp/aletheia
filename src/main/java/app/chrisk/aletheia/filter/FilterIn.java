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
 * FilterIn
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class FilterIn extends JFrame
{
	private final ArrayList<ConfigFilterAbstract> filtersConfig = new ArrayList<>();
	private final ArrayList<RequestFilterAbstract> filters = new ArrayList<>();

	private final ArrayList<RequestFilterAbstract> activeFilters;

	public FilterIn(ArrayList<RequestFilterAbstract> activeFilters)
	{
		this.activeFilters = activeFilters;

		// settings
		this.setTitle("Request filter");
		this.setLocation(100, 100);
		this.setSize(400, 300);
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());

		// tab panel
		JTabbedPane panel = new JTabbedPane();
		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		// add filters
		ArrayList<String> filters = new ArrayList<>();
		filters.add("Cookie");
		filters.add("BasicAuth");
		filters.add("OAuth");
		filters.add("UserAgent");
		filters.add("Process");

		// parse filters
        for (String s : filters) {
            try {
                String classNameConfig = "app.chrisk.aletheia.filter.request." + s + "Config";
                String className = "app.chrisk.aletheia.filter.request." + s;

                Class<?> classConfig = Class.forName(classNameConfig);
                Class<?> classRequest = Class.forName(className);

                ConfigFilterAbstract filterConfig = (ConfigFilterAbstract) classConfig.newInstance();
                RequestFilterAbstract filter = (RequestFilterAbstract) classRequest.newInstance();

                // load config
                for (RequestFilterAbstract activeFilter : activeFilters) {
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

			for (int i = 0; i < filtersConfig.size(); i++) {
				if (filtersConfig.get(i).isActive()) {
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
