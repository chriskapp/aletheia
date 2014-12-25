package com.k42b3.aletheia.search.engine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.k42b3.aletheia.TextPaneOut;
import com.k42b3.aletheia.protocol.Response;
import com.k42b3.aletheia.search.SearchInterface;

public class CssSelector implements SearchInterface
{
	public String getName()
	{
		return "CSS Selector";
	}

	public void search(String search, TextPaneOut out) throws Exception
	{
		if(!search.isEmpty())
		{
			Response response = out.getResponse();
			
			if(response instanceof com.k42b3.aletheia.protocol.http.Response)
			{
				com.k42b3.aletheia.protocol.http.Response httpResponse = (com.k42b3.aletheia.protocol.http.Response) response;

				String html = httpResponse.getBody();
				Document doc = Jsoup.parse(html);
				
				Elements els = doc.select(search);
				StringBuilder result = new StringBuilder();
				
				for(Element el : els)
				{
					result.append(el.outerHtml());
					result.append("\n");
				}

				httpResponse.setBody(result.toString());

				out.update();
			}
		}
	}
}
