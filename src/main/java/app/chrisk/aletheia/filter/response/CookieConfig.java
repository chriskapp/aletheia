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

package app.chrisk.aletheia.filter.response;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import app.chrisk.aletheia.filter.ConfigFilterAbstract;

/**
 * CookieConfig
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class CookieConfig extends ConfigFilterAbstract
{
	private JCheckBox ckbActive;

	public CookieConfig()
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


		this.add(panel);
	}

	public String getName()
	{
		return "Cookie";
	}
	
	public void onLoad(Properties config) 
	{
		this.ckbActive.setSelected(true);
	}

	public Properties onSave() 
	{
		Properties config = new Properties();

		return config;
	}
	
	public boolean isActive()
	{
		return this.ckbActive.isSelected();
	}
}
