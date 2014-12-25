package com.k42b3.aletheia.search.engine;

import com.k42b3.aletheia.TextPaneOut;
import com.k42b3.aletheia.search.SearchFactory;
import com.k42b3.aletheia.search.SearchInterface;

public class Html implements SearchInterface
{
	public String getName()
	{
		return "HTML Search";
	}

	public void search(String search, TextPaneOut out) throws Exception
	{
		// if the search string is an css selector
		if(search.startsWith("css:"))
		{
			SearchFactory.getEngine("CssSelector").search(search.substring(4), out);
		}
		else
		{
			SearchFactory.getEngine("Text").search(search, out);
		}
	}
}
