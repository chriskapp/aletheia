package com.k42b3.aletheia;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.k42b3.aletheia.filter.RequestFilterAbstract;
import com.k42b3.aletheia.filter.ResponseFilterAbstract;

public class Config
{
	private File configFile;

	private ArrayList<RequestFilterAbstract> filtersIn = new ArrayList<RequestFilterAbstract>();
	private ArrayList<ResponseFilterAbstract> filtersOut = new ArrayList<ResponseFilterAbstract>();
	private HashMap<String, String> applications = new HashMap<String, String>();

	private Logger logger = Logger.getLogger("com.k42b3.aletheia");

	public Config(File configFile)
	{
		this.configFile = configFile;

		this.parse();
	}

	public ArrayList<RequestFilterAbstract> getFiltersIn()
	{
		return filtersIn;
	}
	
	public ArrayList<ResponseFilterAbstract> getFiltersOut()
	{
		return filtersOut;
	}

	public HashMap<String, String> getApplications()
	{
		return applications;
	}

	private void parse()
	{
		try
		{
			// read xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(this.configFile);

			Element rootElement = (Element) doc.getDocumentElement();

			rootElement.normalize();

			// parse filters
			NodeList filtersList = doc.getElementsByTagName("filter");

			parseFiltersIn(filtersList);
			parseFiltersOut(filtersList);

			// parse applications
			NodeList applicationsList = doc.getElementsByTagName("application");

			parseApplications(applicationsList);
		}
		catch(Exception e)
		{
			Aletheia.handleException(e);
		}
	}

	private void parseFiltersIn(NodeList filtersList)
	{
		for(int i = 0; i < filtersList.getLength(); i++)
		{
			try
			{
				Element filterElement = (Element) filtersList.item(i);
				
				// in
				if(filterElement.getParentNode().getNodeName().equals("in"))
				{
					String cls = filterElement.getAttribute("name");
					Properties config = new Properties();

					NodeList propertyList = filterElement.getElementsByTagName("property");

					for(int j = 0; j < propertyList.getLength(); j++)
					{
						Element property = (Element) propertyList.item(j);

						config.put(property.getAttribute("name"), property.getTextContent());
					}

					Class c = Class.forName(cls);

					RequestFilterAbstract filter = (RequestFilterAbstract) c.newInstance();
					filter.setConfig(config);

					filtersIn.add(filter);
				}
			}
			catch(Exception e)
			{
				Aletheia.handleException(e);
			}
		}

		if(filtersIn.size() > 0)
		{
			logger.info("Loaded " + filtersIn.size() + " request filter");
		}
	}
	
	private void parseFiltersOut(NodeList filtersList)
	{
		for(int i = 0; i < filtersList.getLength(); i++)
		{
			try
			{
				Element filterElement = (Element) filtersList.item(i);

				// out
				if(filterElement.getParentNode().getNodeName().equals("out"))
				{
					String cls = filterElement.getAttribute("name");
					Properties config = new Properties();

					NodeList propertyList = filterElement.getElementsByTagName("property");

					for(int j = 0; j < propertyList.getLength(); j++)
					{
						Element property = (Element) propertyList.item(j);

						config.put(property.getAttribute("name"), property.getTextContent());
					}

					Class c = Class.forName(cls);

					ResponseFilterAbstract filter = (ResponseFilterAbstract) c.newInstance();
					filter.setConfig(config);

					filtersOut.add(filter);
				}
			}
			catch(Exception e)
			{
				Aletheia.handleException(e);
			}
		}

		if(filtersOut.size() > 0)
		{
			logger.info("Loaded " + filtersOut.size() + " response filter");
		}
	}
	
	private void parseApplications(NodeList filtersList)
	{
		for(int i = 0; i < filtersList.getLength(); i++)
		{
			try
			{
				Element filterElement = (Element) filtersList.item(i);

				// out
				if(filterElement.getParentNode().getNodeName().equals("applications"))
				{
					String contentType = filterElement.getAttribute("contentType");
					String path = filterElement.getAttribute("path");

					if(!contentType.isEmpty() && !path.isEmpty())
					{
						applications.put(contentType, path);
					}
				}
			}
			catch(Exception e)
			{
				Aletheia.handleException(e);
			}
		}

		if(applications.size() > 0)
		{
			logger.info("Loaded " + applications.size() + " application associations");
		}
	}
}
