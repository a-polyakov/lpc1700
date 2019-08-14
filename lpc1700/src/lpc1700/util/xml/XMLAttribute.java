package lpc1700.util.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 15.11.2007
 * Time: 14:39:26
 * TODO.
 */
public class XMLAttribute
{
	private String name;
	private String value;

	public XMLAttribute(String name, String value)
	{
		this.name = name;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public void save(XMLStreamWriter xml) throws XMLStreamException
	{
		xml.writeAttribute(name, value);
	}

}
