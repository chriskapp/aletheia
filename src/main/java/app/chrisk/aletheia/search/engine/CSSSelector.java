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

package app.chrisk.aletheia.search.engine;

import app.chrisk.aletheia.TextPaneOut;
import app.chrisk.aletheia.protocol.http.Response;
import app.chrisk.aletheia.search.SearchInterface;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * CSSSelector
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class CSSSelector implements SearchInterface
{
	public String getName()
	{
		return "CSS Selector";
	}

	public void search(String search, TextPaneOut out) throws Exception
	{
		if (!search.isEmpty()) {
			app.chrisk.aletheia.protocol.Response response = out.getResponse();

			if (response instanceof Response httpResponse) {
                String html = httpResponse.getBody();
				Document doc = Jsoup.parse(html);
				
				Elements els = doc.select(search);
				StringBuilder result = new StringBuilder();
				
				for (Element el : els) {
					result.append(el.outerHtml());
					result.append("\n");
				}

				httpResponse.setBody(result.toString());

				out.update();
			}
		}
	}
}
