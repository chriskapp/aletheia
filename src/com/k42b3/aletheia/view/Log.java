/**
 * aletheia
 * A browser like application to send raw http requests. It is designed for 
 * debugging and finding security issues in web applications. For the current 
 * version and more informations visit <http://aletheia.k42b3.com>
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

package com.k42b3.aletheia.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

/**
 * Log
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Log extends JFrame
{
	private JTextArea txtLog;

	private Logger logger = Logger.getLogger("com.k42b3.aletheia");

	public Log()
	{
		// settings
		this.setTitle("Log");
		this.setLocation(100, 100);
		this.setSize(360, 400);
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		this.setFont(new Font("Monospaced", Font.PLAIN, 12));

		// tab panel
		txtLog = new JTextArea();
		txtLog.setEditable(false);
		txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
		txtLog.setBackground(new Color(255, 255, 255));
		txtLog.setForeground(new Color(0, 0, 0));

		this.add(new JScrollPane(txtLog), BorderLayout.CENTER);


		// buttons
		JPanel panelButtons = new JPanel();

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		panelButtons.setLayout(fl);

		JButton btnReset = new JButton("Reset");
		btnReset.setMnemonic(java.awt.event.KeyEvent.VK_R);
		btnReset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				txtLog.setText("");
			}

		});

		panelButtons.add(btnReset);

		this.add(panelButtons, BorderLayout.SOUTH);
	}
	
	public void append(LogRecord rec)
	{
		txtLog.setText(txtLog.getText() + rec.getLevel() + ": " + rec.getMessage() + "\n");
	}
}
