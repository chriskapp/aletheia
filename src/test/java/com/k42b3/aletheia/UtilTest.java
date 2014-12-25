package com.k42b3.aletheia;

import junit.framework.TestCase;

import com.k42b3.aletheia.protocol.http.Util;

public class UtilTest extends TestCase
{
	public void testAppendQuery()
	{
		assertEquals("http://foo.com?foo=bar&bar=foo", Util.appendQuery("http://foo.com?foo=bar", "bar=foo"));
		assertEquals("http://foo.com?bar=foo", Util.appendQuery("http://foo.com", "bar=foo"));
	}

	public void testResolveHref() throws Exception
	{
		assertEquals("http://bar.com/foo.php", Util.resolveHref("http://www.foobar.com/foo/bar/", "//bar.com/foo.php"));
		assertEquals("http://bar.com/foo.php", Util.resolveHref("http://www.foobar.com/foo/bar/", "http://bar.com/foo.php"));
		assertEquals("http://www.foobar.com/foo/bar/foo.php", Util.resolveHref("http://www.foobar.com/foo/bar/", "foo.php"));
		assertEquals("http://www.foobar.com/foo.php", Util.resolveHref("http://www.foobar.com/foo/bar/", "/foo.php"));
		assertEquals("http://www.foobar.com/foo/bar/foo.php", Util.resolveHref("http://www.foobar.com/foo/bar/", "./foo.php"));
		assertEquals("http://www.foobar.com/foo/bar/foo.php", Util.resolveHref("http://www.foobar.com/foo/bar/foo.php", "./foo.php"));
		assertEquals("http://www.foobar.com/foo/foo.php", Util.resolveHref("http://www.foobar.com/foo/bar/", "../foo.php"));
		assertEquals("http://www.foobar.com/foo/foo.php", Util.resolveHref("http://www.foobar.com/foo/bar.php", "foo.php"));
		assertEquals("http://www.foobar.com/foo/bar.php?foo=bar", Util.resolveHref("http://www.foobar.com/foo/bar.php", "?foo=bar"));
		assertEquals("http://www.foobar.com/foo/bar.php?foo=bar", Util.resolveHref("http://www.foobar.com/foo/bar.php?bar=foo", "?foo=bar"));
	}
}
