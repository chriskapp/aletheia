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
import app.chrisk.aletheia.search.SearchFactory;
import app.chrisk.aletheia.search.SearchInterface;

/**
 * HTML
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class HTML implements SearchInterface
{
	public String getName()
	{
		return "HTML Search";
	}

	public void search(String search, TextPaneOut out) throws Exception
	{
		// if the search string is a css selector
		if (search.startsWith("css:")) {
			SearchFactory.getEngine("CSSSelector").search(search.substring(4), out);
		} else {
			SearchFactory.getEngine("Text").search(search, out);
		}
	}
}
