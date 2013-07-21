package com.k42b3.aletheia.search.engine;

import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import com.k42b3.aletheia.TextPaneOut;
import com.k42b3.aletheia.search.SearchInterface;

public class Text implements SearchInterface
{
	public String getName()
	{
		return "Text Search";
	}

	public void search(String search, TextPaneOut out) throws Exception
	{
		if(!search.isEmpty())
		{
			SearchContext context = new SearchContext();
			context.setSearchFor(search);
			context.setMatchCase(false);
			context.setRegularExpression(false);
			context.setSearchForward(true);
			context.setWholeWord(false);

			boolean found = SearchEngine.find(out, context);

			if(!found)
			{
				out.setCaretPosition(0);

				SearchEngine.find(out, context);
			}
		}
	}
}
