package com.k42b3.aletheia.response.process;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.k42b3.aletheia.Aletheia;
import com.k42b3.aletheia.processor.ProcessPropertiesAbstract;
import com.k42b3.aletheia.processor.ResponseProcessorInterface;
import com.k42b3.aletheia.protocol.Response;
import com.k42b3.aletheia.protocol.http.Request;
import com.k42b3.aletheia.protocol.http.Util;

public class ReCaptcha extends JFrame implements ResponseProcessorInterface
{
	private JPanel panel;
	private URL baseUrl;

	private String recaptchaChallengeField;
	private URL imageUrl;

	private JTextField txtAnswer;
	private JTextField txtChallenge;
	
	public ReCaptcha()
	{
		super();

		// settings
		this.setTitle("ReCaptcha");
		this.setLocation(100, 100);
		this.setPreferredSize(new Dimension(360, 220));
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());

		// panel
		panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("Loading ..."));

		this.add(panel, BorderLayout.CENTER);
		
		// buttons
		JPanel panelButtons = new JPanel();

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		panelButtons.setLayout(fl);

		JButton btnInsert = new JButton("Insert");
		btnInsert.setMnemonic(java.awt.event.KeyEvent.VK_I);
		btnInsert.addActionListener(new InsertHandler());

		panelButtons.add(btnInsert);

		this.add(panelButtons, BorderLayout.SOUTH);


		this.pack();
	}

	public void process(URL url, Response response, Properties properties) throws Exception
	{
		if(response instanceof com.k42b3.aletheia.protocol.http.Response)
		{
			com.k42b3.aletheia.protocol.http.Response httpResponse = (com.k42b3.aletheia.protocol.http.Response) response;
			
			// reset
			this.reset();

			// set base url
			baseUrl = url;

			// parse html
			String html = httpResponse.getBody();
			Document doc = Jsoup.parse(html);

			// find noscript iframe
			URL iframeSrc = null;
			Elements els = doc.getElementsByTag("iframe");

			for(Element iframe : els)
			{
				String src = iframe.attr("src");

				if(!src.isEmpty() && src.indexOf("/recaptcha/api/noscript") != -1)
				{
					iframeSrc = new URL(src);
					break;
				}
			}
			
			if(iframeSrc != null)
			{
				// request
				DefaultHttpClient client = new DefaultHttpClient();
				HttpUriRequest request = new HttpGet(iframeSrc.toString());
				HttpResponse resp = client.execute(request);
				String body = EntityUtils.toString(resp.getEntity());
				
				// parse html
				doc = Jsoup.parse(body);
				
				// get challenge field
				Element input = doc.getElementById("recaptcha_challenge_field");
				
				if(input != null)
				{
					recaptchaChallengeField = input.attr("value");
				}
				
				// get image
				els = doc.getElementsByTag("img");

				if(els.size() > 0)
				{
					Element img = els.get(0);

					try
					{
						imageUrl = new URL(Util.resolveHref(iframeSrc.toString(), img.attr("src")));
					}
					catch(Exception e)
					{
						Aletheia.handleException(e);
					}
				}

				// build elements
				buildElements();
			}
			else
			{
				throw new Exception("Found no noscript iframe");
			}
		}

		// set visible
		this.pack();
		this.setVisible(true);
	}

	public ProcessPropertiesAbstract getProperties()
	{
		return null;
	}
	
	private void reset()
	{
		this.panel.removeAll();
	}

	private void insert()
	{
		// insert request
		try
		{
			Request request = (Request) Aletheia.getInstance().getActiveIn().getRequest();
			StringBuilder response = new StringBuilder();

			response.append("recaptcha_challenge_field=" + URLEncoder.encode(txtChallenge.getText(), "UTF-8"));
			response.append("&");
			response.append("recaptcha_response_field=" + URLEncoder.encode(txtAnswer.getText(), "UTF-8"));

			URL url = new URL(Aletheia.getInstance().getActiveUrl().getText());

			request.setLine("POST", url.getPath());
			request.setHeader("Content-Type", "application/x-www-form-urlencoded");
			request.setBody(response.toString());

			Aletheia.getInstance().getActiveIn().update();
		}
		catch(Exception e)
		{
			Aletheia.handleException(e);
		}

		this.setVisible(false);
	}

	private void buildElements()
	{
		// captcha
		try
		{
			DefaultHttpClient client = new DefaultHttpClient();
			HttpUriRequest request = new HttpGet(imageUrl.toString());
			HttpResponse response = client.execute(request);

			// read image
			byte[] image = EntityUtils.toByteArray(response.getEntity());
			JLabel label = new JLabel(new ImageIcon(image));
			
			panel.add(label);
		}
		catch(IOException e)
		{
			panel.add(new JLabel(e.getMessage()));
		}

		// answer
		JLabel lblAnswer = new JLabel("Answer");
		lblAnswer.setPreferredSize(new Dimension(80, 24));
		
		txtAnswer = new JTextField("");
		txtAnswer.setPreferredSize(new Dimension(260, 24));
		txtAnswer.requestFocusInWindow();
		
		panel.add(lblAnswer);
		panel.add(txtAnswer);
		
		// challenge
		JLabel lblChallenge = new JLabel("Challenge");
		lblChallenge.setPreferredSize(new Dimension(80, 24));
		
		txtChallenge = new JTextField(recaptchaChallengeField);
		txtChallenge.setPreferredSize(new Dimension(260, 24));
		
		panel.add(lblChallenge);
		panel.add(txtChallenge);

		panel.validate();
	}

	private class InsertHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			insert();
		}
	}
}
