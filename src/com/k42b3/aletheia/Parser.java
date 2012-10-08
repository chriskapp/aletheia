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

package com.k42b3.aletheia;

import java.util.logging.Logger;

/**
 * Parser
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://aletheia.k42b3.com
 */
public class Parser
{
	protected Logger logger = Logger.getLogger("com.k42b3.aletheia");

	public static String getAttribute(String name, String content)
	{
		StringBuilder value = new StringBuilder();
		int sPos = -1;
		boolean noWhiteSpaces = false;
		boolean inVal = false;

		for(int i = 0; i < content.length(); i++)
		{
			if(inVal)
			{
				if(content.charAt(i) == '"' || content.charAt(i) == '\'' || content.charAt(i) == '>')
				{
					break;
				}

				if(noWhiteSpaces && Character.isWhitespace(content.charAt(i)))
				{
					break;
				}

				value.append(content.charAt(i));
			}
			else
			{
				if(Parser.startsWith(name + "=", i, content))
				{
					sPos = i + name.length();

					if(content.charAt(sPos + 1) != '"' && content.charAt(sPos + 1) != '\'')
					{
						noWhiteSpaces = true;
					}
					else
					{
						sPos++;
					}
				}
			}

			if(i == sPos)
			{
				inVal = true;
			}
		}

		return value.toString();
	}

	public static String getTag(int index, String content)
	{
		int pos = content.indexOf('>', index);

		if(pos != -1)
		{
			return content.substring(index, pos + 1);
		}

		return null;
	}
	
	public static boolean startsWith(String phrase, int index, String content)
	{
		for(int i = 0; i < phrase.length(); i++)
		{
			if(Character.toLowerCase(content.charAt(index + i)) != Character.toLowerCase(phrase.charAt(i)))
			{
				return false;
			}
		}

		return true;	
	}
}
