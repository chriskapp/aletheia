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

package app.chrisk.aletheia.filter.request;

import app.chrisk.aletheia.Aletheia;
import app.chrisk.aletheia.filter.ConfigFilterAbstract;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * UserAgentConfig
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class UserAgentConfig extends ConfigFilterAbstract
{
	private final JCheckBox ckbActive;
	private final JComboBox<AgentModel> cboAgent;

	public UserAgentConfig()
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


		// agent
		JPanel panelAgent = new JPanel();
		panelAgent.setLayout(new FlowLayout());

		JLabel lblAgent = new JLabel("User agent:");
		lblAgent.setPreferredSize(new Dimension(100, 24));
		panelAgent.add(lblAgent);

		this.cboAgent = new JComboBox();
		this.cboAgent.setPreferredSize(new Dimension(200, 24));
		panelAgent.add(this.cboAgent);

		panel.add(panelAgent);

		this.add(panel);

		// add agents
		ArrayList<AgentEntry> agents = new ArrayList<>();
		agents.add(new AgentEntry("Aletheia " + Aletheia.VERSION, "Aletheia/" + Aletheia.VERSION));
        agents.add(new AgentEntry("Safari 17.10, Mac OS X", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.10 Safari/605.1.1"));
		agents.add(new AgentEntry("Chrome 113.0.0, Mac OS X", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.3"));
		agents.add(new AgentEntry("Chrome 134.0.0, Linux", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.3"));
        agents.add(new AgentEntry("Chrome 134.0.0, Windows", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.3"));
		agents.add(new AgentEntry("Edge 134.0.0, Windows", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0."));
        agents.add(new AgentEntry("Opera 117.0.0, Windows", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36 OPR/117.0.0."));
		agents.add(new AgentEntry("Lynx 2.8.7", "Lynx/2.8.7dev.4 libwww-FM/2.14 SSL-MM/1.4.1 OpenSSL/0.9.8d"));
		agents.add(new AgentEntry("Googlebot 2.1", "Googlebot/2.1 (+http://www.googlebot.com/bot.html)"));

		this.cboAgent.setModel(new AgentModel(agents));
	}

	public String getName()
	{
		return "User Agent";
	}

	public void onLoad(Properties config) 
	{
		this.ckbActive.setSelected(true);

		this.cboAgent.setSelectedItem(new AgentEntry(config.getProperty("agent")));
	}

	public Properties onSave() 
	{
		Properties config = new Properties();

		Object item = this.cboAgent.getSelectedItem();
		if (item != null) {
			config.setProperty("agent", ((AgentEntry) item).getKey());
		}

		return config;
	}
	
	public boolean isActive()
	{
		return this.ckbActive.isSelected();
	}

	class AgentModel implements ComboBoxModel
	{
		ArrayList<AgentEntry> agents = new ArrayList<>();
		ArrayList<ListDataListener> listener = new ArrayList<>();
		
		private Object selected;

		public AgentModel(ArrayList<AgentEntry> agents)
		{
			this.agents.addAll(agents);
		}

		public AgentModel()
		{
			this(null);
		}

		public Object getSelectedItem() 
		{
			return this.selected;
		}

		public void setSelectedItem(Object obj) 
		{
			this.selected = obj;
		}

		public void addListDataListener(ListDataListener l) 
		{
			this.listener.add(l);
		}

		public Object getElementAt(int index) 
		{
			return this.agents.get(index);
		}

		public int getSize() 
		{
			return this.agents.size();
		}

		public void removeListDataListener(ListDataListener l) 
		{
			this.listener.remove(l);
		}
	}
	
	static class AgentEntry
	{
		private final String key;
		private final String value;

		public AgentEntry(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		public AgentEntry(String key)
		{
			this(key, null);
		}

		public String getKey()
		{
			return this.key;
		}

		public String getValue()
		{
			return this.value;
		}

		public String toString()
		{
			return this.getKey();
		}

		public boolean equals(Object obj)
		{
			if (obj instanceof AgentEntry) {
				AgentEntry entry = (AgentEntry) obj;

				return this.getKey().equals(entry.getKey());
			} else {
				return super.equals(obj);
			}
		}
	}
}
