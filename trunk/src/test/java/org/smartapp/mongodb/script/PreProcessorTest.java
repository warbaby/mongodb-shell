package org.smartapp.mongodb.script;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PreProcessorTest {

	private PreProcessor p;

	@Before
	public void setUp() throws Exception {
		p = new PreProcessor();
	}

	@After
	public void tearDown() throws Exception {
		p = null;
	}

	@Test
	public void testPreprocessDummy() {
		assertEquals("test", p.preprocess("test"));
	}
	
	@Test
	public void testPreprocessEmpty() {
		assertEquals("", p.preprocess(""));
	}

	@Test
	public void testPreprocessNull() {
		assertNull(p.preprocess(null));
	}
	
	
	
	@Test
	public void testReplace() {
		assertEquals("use('mydb');", p.preprocess("use mydb;"));
		assertEquals("use('mydb');", p.preprocess("use   mydb;"));
		assertEquals(" use('mydb'); ", p.preprocess(" use mydb; "));
		assertEquals("var x=1;\nuse('mydb');\n y= 5; ", p.preprocess("var x=1;\nuse mydb;\n y= 5; "));
	}
	
	
	@Test
	public void testReplaceMultiline() {
		String script = 
			"use xyz;\n" +
			"var a = db.abc.count();\n" +
			"use test123;\n" +
			"var b = a + db.col.count();";
		
		String expected = 
			"use('xyz');\n" +
			"var a = db.abc.count();\n" +
			"use('test123');\n" +
			"var b = a + db.col.count();";
		
		assertEquals(expected, p.preprocess(script));
		
	}
	
	
	
	

}
