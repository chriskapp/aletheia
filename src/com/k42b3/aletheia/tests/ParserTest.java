package com.k42b3.aletheia.tests;

import junit.framework.TestCase;

import com.k42b3.aletheia.Parser;
import com.k42b3.aletheia.protocol.http.Util;

public class ParserTest extends TestCase
{
	public void testGetAttribute()
	{
		assertEquals("foo", Parser.getAttribute("name", "<input name=\"foo\" value=\"bar\" />"));
		assertEquals("foo", Parser.getAttribute("name", "<input name='foo' value='bar' />"));
		assertEquals("foo", Parser.getAttribute("name", "<input name=foo value=bar />"));
		assertEquals("foo", Parser.getAttribute("name", "<input name=\"foo\"value=\"bar\" />"));
		assertEquals("foo", Parser.getAttribute("name", "<input name='foo'value='bar' />"));
		assertEquals("foo<bar", Parser.getAttribute("name", "<input name=\"foo<bar\" value=\"bar\" />"));
		assertEquals("foo<bar", Parser.getAttribute("name", "<input name='foo<bar' value=\"bar\" />"));
		assertEquals("foo>bar", Parser.getAttribute("name", "<input name=\"foo>bar\" value=\"bar\" />"));
		assertEquals("foo>bar", Parser.getAttribute("name", "<input name='foo>bar' value=\"bar\" />"));
		assertEquals("foo'bar", Parser.getAttribute("name", "<input name=\"foo'bar\" value=\"bar\" />"));
		assertEquals("foo\"bar", Parser.getAttribute("name", "<input name='foo\"bar' value=\"bar\" />"));
	}
}
