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

package com.k42b3.aletheia.filter.request;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListDataListener;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.filter.ConfigFilterAbstract;

/**
 * UserAgentConfig
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class UserAgentConfig extends ConfigFilterAbstract
{
	private JCheckBox ckbActive;
	private JComboBox cboAgent;

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
		ArrayList<AgentEntry> agents = new ArrayList<AgentEntry>();

		agents.add(new AgentEntry("Aletheia " + Aletheia.VERSION, "Aletheia/" + Aletheia.VERSION));
		agents.add(new AgentEntry("Firefox 4.0", "Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.3) Gecko/20100401 Firefox/4.0 (.NET CLR 3.5.30729)"));
		agents.add(new AgentEntry("Firefox 3.8", "Mozilla/5.0 (X11; U; Linux i686; pl-PL; rv:1.9.0.2) Gecko/20121223 Ubuntu/9.25 (jaunty) Firefox/3.8"));
		agents.add(new AgentEntry("Internet Explorer 8", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.2; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)"));
		agents.add(new AgentEntry("Internet Explorer 7", "Mozilla/5.0 (Windows; U; MSIE 7.0; Windows NT 6.0; en-US)"));
		agents.add(new AgentEntry("Internet Explorer 6", "Mozilla/5.0 (Windows; U; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)"));
		agents.add(new AgentEntry("Lynx 2.8.7", "Lynx/2.8.7dev.4 libwww-FM/2.14 SSL-MM/1.4.1 OpenSSL/0.9.8d"));
		agents.add(new AgentEntry("Opera 9.70", "Opera/9.70 (Linux i686 ; U; en) Presto/2.2.1"));
		agents.add(new AgentEntry("Safari 5.0", "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_5_8; ja-jp) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16"));
		agents.add(new AgentEntry("Safari 4.1", "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_4_11; nl-nl) AppleWebKit/533.16 (KHTML, like Gecko) Version/4.1 Safari/533.16"));
		agents.add(new AgentEntry("Konqueror 4.4", "Mozilla/5.0 (compatible; Konqueror/4.4; Linux) KHTML/4.4.1 (like Gecko) Fedora/4.4.1-1.fc12"));
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
		
		if(item != null)
		{
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
		ArrayList<AgentEntry> agents = new ArrayList<AgentEntry>();
		ArrayList<ListDataListener> listener = new ArrayList<ListDataListener>();
		
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
	
	class AgentEntry
	{
		private String key;
		private String value;

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
			if(obj instanceof AgentEntry)
			{
				AgentEntry entry = (AgentEntry) obj;

				return this.getKey().equals(entry.getKey());
			}
			else
			{
				return super.equals(obj);
			}
		}
	}
}
