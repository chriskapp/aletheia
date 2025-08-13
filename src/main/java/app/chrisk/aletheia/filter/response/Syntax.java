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

import app.chrisk.aletheia.protocol.http.Response;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import app.chrisk.aletheia.Aletheia;
import app.chrisk.aletheia.filter.ResponseFilterAbstract;

/**
 * Application
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public class Syntax extends ResponseFilterAbstract
{
	public void exec(app.chrisk.aletheia.protocol.Response response) throws Exception
	{
		if(response instanceof Response)
		{
			Response httpResponse = (Response) response;

			String contentType = httpResponse.getHeader("Content-Type");
			RSyntaxTextArea textarea = (RSyntaxTextArea) Aletheia.getInstance().getActiveOut();

			if(contentType.indexOf("text/html") != -1)
			{
				textarea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
			}
			else if(contentType.indexOf("text/css") != -1)
			{
				textarea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);
			}
			else if(contentType.indexOf("/javascript") != -1)
			{
				textarea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
			}
			else if(contentType.indexOf("/json") != -1)
			{
				textarea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
			}
			else if(contentType.indexOf("/xml") != -1)
			{
				textarea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
			}
		}
	}
}
