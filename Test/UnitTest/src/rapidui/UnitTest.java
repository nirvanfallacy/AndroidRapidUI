package rapidui;

public class UnitTest extends android.test.AndroidTestCase {
	public void testCamelCaseToUnderlinedLowerCase() {
		assertEquals("test", ResourceUtils.toLowerUnderscored("test"));
		assertEquals("camel_case", ResourceUtils.toLowerUnderscored("camelCase"));
		assertEquals("pascal_case", ResourceUtils.toLowerUnderscored("PascalCase"));
		assertEquals("underlined_camel_case", ResourceUtils.toLowerUnderscored("Underlined_Camel_Case"));
		assertEquals("xml_document", ResourceUtils.toLowerUnderscored("XMLDocument"));
		assertEquals("simple_xml_parser", ResourceUtils.toLowerUnderscored("SimpleXMLParser"));
		assertEquals("ab123", ResourceUtils.toLowerUnderscored("AB123"));
		assertEquals("ab123", ResourceUtils.toLowerUnderscored("ab123"));
		assertEquals("a_b123", ResourceUtils.toLowerUnderscored("aB123"));
		assertEquals("html4_document", ResourceUtils.toLowerUnderscored("HTML4Document"));
	}
}
