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

package app.chrisk.aletheia;

import app.chrisk.aletheia.filter.RequestFilterAbstract;
import app.chrisk.aletheia.filter.ResponseFilterAbstract;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Config
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Config
{
	private final File configFile;

	private final ArrayList<RequestFilterAbstract> filtersIn = new ArrayList<>();
	private final ArrayList<ResponseFilterAbstract> filtersOut = new ArrayList<>();
	private final HashMap<String, String> applications = new HashMap<>();
	private final ArrayList<URL> bookmarks = new ArrayList<>();

	private final Logger logger = Logger.getLogger("app.chrisk.aletheia");

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

	public ArrayList<URL> getBookmarks()
	{
		return bookmarks;
	}

	public boolean addBookmark(URL url)
	{
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(this.configFile);

			// check whether bookmark exist
			Element bookmark = (Element) doc.getElementsByTagName("bookmarks").item(0);
			
			if (bookmark != null) {
				NodeList bookmarks = bookmark.getChildNodes();
				boolean removed = false;

				for (int i = 0; i < bookmarks.getLength(); i++) {
					if (bookmarks.item(i) instanceof Element) {
						Element existingbookmark = (Element) bookmarks.item(i);

						// if the bookmark exists remove it
						if (url.toString().equals(existingbookmark.getAttribute("url"))) {
							bookmark.removeChild(existingbookmark);

							this.bookmarks.remove(url);

							removed = true;
							break;
						}
					}
				}

				// if the bookmark dosent exist add it
				if (!removed) {
					Element newBookmark = doc.createElement("bookmark");
					newBookmark.setAttribute("url", url.toString());

					bookmark.appendChild(newBookmark);

					this.bookmarks.add(url);
				}

				// save
				DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
				DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");

				LSSerializer writer = impl.createLSSerializer();
				LSOutput output = impl.createLSOutput();

				output.setByteStream(Files.newOutputStream(configFile.toPath()));

				writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
				writer.write(doc, output);
				
				return !removed;
			} else {
				throw new Exception("Found no bookmarks tag");
			}
		} catch (Exception e) {
			Aletheia.handleException(e);
		}

		return false;
	}

	public boolean hasBookmark(URL url)
	{
        for (URL bookmark : bookmarks) {
            if (bookmark.equals(url)) {
                return true;
            }
        }

		return false;
	}

	private void parse() {
		try {
            if (!this.configFile.isFile()) {
                // try to write default config if not available
                Files.writeString(this.configFile.toPath(), getConfigXml());
            }

            // read xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(this.configFile);

			Element rootElement = doc.getDocumentElement();

			rootElement.normalize();

			// parse filters
			NodeList filtersList = doc.getElementsByTagName("filter");

			parseFiltersIn(filtersList);
			parseFiltersOut(filtersList);

			// parse applications
			NodeList applicationsList = doc.getElementsByTagName("application");

			parseApplications(applicationsList);
			
			// parse applications
			NodeList bookmarksList = doc.getElementsByTagName("bookmark");

			parseBookmarks(bookmarksList);
		} catch(Exception e) {
			Aletheia.handleException(e);
		}
	}

    private static String getConfigXml() {
        String configXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        configXml += "<aletheia>\n";
        configXml += "    <filters>\n";
        configXml += "        <in>\n";
        configXml += "            <filter name=\"app.chrisk.aletheia.filter.request.Cookie\"/>\n";
        configXml += "            <filter name=\"app.chrisk.aletheia.filter.request.UserAgent\">\n";
        configXml += "                <property name=\"agent\">Aletheia 0.2.0</property>\n";
        configXml += "            </filter>\n";
        configXml += "        </in>\n";
        configXml += "        <out>\n";
        configXml += "            <filter name=\"app.chrisk.aletheia.filter.response.Cookie\"/>\n";
        configXml += "            <filter name=\"app.chrisk.aletheia.filter.response.Location\"/>\n";
        configXml += "            <filter name=\"app.chrisk.aletheia.filter.response.Application\"/>\n";
        configXml += "        </out>\n";
        configXml += "    </filters>\n";
        configXml += "    <applications>\n";
        configXml += "    </applications>\n";
        configXml += "    <bookmarks>\n";
        configXml += "    </bookmarks>\n";
        configXml += "</aletheia>\n";
        return configXml;
    }

    private void parseFiltersIn(NodeList filtersList)
	{
		for (int i = 0; i < filtersList.getLength(); i++) {
			try {
				Element filterElement = (Element) filtersList.item(i);
				
				// in
				if (filterElement.getParentNode().getNodeName().equals("in")) {
					String cls = filterElement.getAttribute("name");
					Properties config = new Properties();

					NodeList propertyList = filterElement.getElementsByTagName("property");

					for (int j = 0; j < propertyList.getLength(); j++) {
						Element property = (Element) propertyList.item(j);

						config.put(property.getAttribute("name"), property.getTextContent());
					}

					Class<?> c = Class.forName(cls);

					RequestFilterAbstract filter = (RequestFilterAbstract) c.getConstructor().newInstance();
					filter.setConfig(config);

					filtersIn.add(filter);
				}
			} catch(Exception e) {
				Aletheia.handleException(e);
			}
		}

		if (!filtersIn.isEmpty()) {
			logger.info("Loaded " + filtersIn.size() + " request filter");
		}
	}
	
	private void parseFiltersOut(NodeList filtersList)
	{
		for (int i = 0; i < filtersList.getLength(); i++) {
			try {
				Element filterElement = (Element) filtersList.item(i);

				// out
				if (filterElement.getParentNode().getNodeName().equals("out")) {
					String cls = filterElement.getAttribute("name");
					Properties config = new Properties();

					NodeList propertyList = filterElement.getElementsByTagName("property");

					for(int j = 0; j < propertyList.getLength(); j++)
					{
						Element property = (Element) propertyList.item(j);

						config.put(property.getAttribute("name"), property.getTextContent());
					}

					Class<?> c = Class.forName(cls);

					ResponseFilterAbstract filter = (ResponseFilterAbstract) c.getConstructor().newInstance();
					filter.setConfig(config);

					filtersOut.add(filter);
				}
			} catch(Exception e) {
				Aletheia.handleException(e);
			}
		}

		if (!filtersOut.isEmpty()) {
			logger.info("Loaded " + filtersOut.size() + " response filter");
		}
	}
	
	private void parseApplications(NodeList applicationsList)
	{
		for (int i = 0; i < applicationsList.getLength(); i++) {
			try {
				Element applicationElement = (Element) applicationsList.item(i);

				// out
				if (applicationElement.getParentNode().getNodeName().equals("applications")) {
					String contentType = applicationElement.getAttribute("contentType");
					String path = applicationElement.getAttribute("path");

					if (!contentType.isEmpty() && !path.isEmpty()) {
						applications.put(contentType, path);
					}
				}
			} catch(Exception e) {
				Aletheia.handleException(e);
			}
		}

		if (!applications.isEmpty()) {
			logger.info("Loaded " + applications.size() + " application associations");
		}
	}

	private void parseBookmarks(NodeList bookmarksList)
	{
		for (int i = 0; i < bookmarksList.getLength(); i++) {
			try {
				Element bookmarkElement = (Element) bookmarksList.item(i);

				// out
				if (bookmarkElement.getParentNode().getNodeName().equals("bookmarks")) {
					String url = bookmarkElement.getAttribute("url");

					if (!url.isEmpty()) {
						try {
							bookmarks.add(new URL(url));
						} catch(MalformedURLException ignored) {
						}
					}
				}
			} catch(Exception e) {
				Aletheia.handleException(e);
			}
		}

		if (!bookmarks.isEmpty()) {
			logger.info("Loaded " + bookmarks.size() + " bookmarks");
		}
	}
}
