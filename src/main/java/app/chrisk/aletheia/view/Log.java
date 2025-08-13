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

package app.chrisk.aletheia.view;

import javax.swing.*;
import java.awt.*;
import java.util.logging.LogRecord;

/**
 * Log
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Log extends JFrame
{
	private final JTextArea txtLog;

	public Log()
	{
		// settings
		this.setTitle("Log");
		this.setLocation(100, 100);
		this.setPreferredSize(new Dimension(360, 400));
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
		btnReset.addActionListener(e -> txtLog.setText(""));

		panelButtons.add(btnReset);

		this.add(panelButtons, BorderLayout.SOUTH);
	}
	
	public void append(LogRecord rec)
	{
		txtLog.setText(txtLog.getText() + rec.getLevel() + ": " + rec.getMessage() + "\n");
	}
}
