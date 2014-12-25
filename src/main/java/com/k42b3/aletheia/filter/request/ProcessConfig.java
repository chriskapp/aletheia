/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more informations visit <http://code.google.com/p/aletheia>
 * 
 * Copyright (c) 2010-2015 Christoph Kappestein <k42b3.x@gmail.com>
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

package com.k42b3.aletheia.filter.request;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.k42b3.aletheia.filter.ConfigFilterAbstract;

/**
 * ProcessConfig
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com/
 */
public class ProcessConfig extends ConfigFilterAbstract
{
	private JCheckBox ckbActive;
	private JTextField txtCmd;

	public ProcessConfig()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));


		// active
		JPanel panelActive = new JPanel();
		panelActive.setLayout(new FlowLayout());

		JLabel lblActive = new JLabel("Active:");
		lblActive.setPreferredSize(new Dimension(100, 24));
		panelActive.add(lblActive);

		this.ckbActive = new JCheckBox();
		this.ckbActive.setPreferredSize(new Dimension(200, 24));
		panelActive.add(this.ckbActive);

		panel.add(panelActive);


		// cmd
		JPanel panelCmd = new JPanel();
		panelCmd.setLayout(new FlowLayout());

		JLabel lblCmd = new JLabel("Cmd:");
		lblCmd.setPreferredSize(new Dimension(100, 24));
		panelCmd.add(lblCmd);

		this.txtCmd = new JTextField();
		this.txtCmd.setPreferredSize(new Dimension(200, 24));
		panelCmd.add(this.txtCmd);

		panel.add(panelCmd);


		this.add(panel);
	}

	public String getName()
	{
		return "Process";
	}

	public void onLoad(Properties config) 
	{
		this.ckbActive.setSelected(true);

		this.txtCmd.setText(config.getProperty("cmd"));
	}

	public Properties onSave() 
	{
		Properties config = new Properties();
		
		config.setProperty("cmd", this.txtCmd.getText());

		return config;
	}
	
	public boolean isActive()
	{
		return this.ckbActive.isSelected();
	}
}
