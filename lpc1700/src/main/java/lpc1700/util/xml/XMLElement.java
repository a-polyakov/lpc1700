package lpc1700.util.xml;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 15.11.2007
 * Time: 14:37:01
 * TODO.
 */
public class XMLElement
{
	private String name;
	private XMLAttribute attributes[];
	private int countAttributes;
	private XMLElement elements[];
	private int countElements;
	private String value;

	public XMLElement()
	{
		this(null);
	}

	public XMLElement(String name)
	{
		this.name = name;
		attributes = new XMLAttribute[10];
		countAttributes = 0;
		elements = new XMLElement[10];
		countElements = 0;
		value = null;
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

	public int getCountAttribut()
	{
		return countAttributes;
	}

	public XMLAttribute getAttribute(int indexAttribute)
	{
		if (indexAttribute >= 0 && indexAttribute < getCountAttribut())
			return attributes[indexAttribute];
		else
			return null;
	}

	public XMLAttribute getAttribute(String name)
	{
		if (name != null)
		{
			int l = getCountAttribut();
			for (int i = 0; i < l; i++)
				if (name.equals(attributes[i].getName()))
					return attributes[i];
		}
		return null;
	}

	public XMLAttribute[] getAttributes()
	{
		return Arrays.copyOf(attributes, countAttributes);
	}

	public void setAtttibute(XMLAttribute attribute, int indexAttribute)
	{
		if (indexAttribute >= 0 && indexAttribute < getCountAttribut())
			attributes[indexAttribute] = attribute;
	}

	public void addAttribute(XMLAttribute attribute)
	{
		if (attribute != null)
		{
			countAttributes++;
			if (countAttributes >= attributes.length)
				setSizeAttribut(attributes.length + 10);
			attributes[countAttributes - 1] = attribute;
		}
	}

	public int getCountElements()
	{
		return countElements;
	}

	public XMLElement getElement(int indexElement)
	{
		if (indexElement >= 0 && indexElement < countElements)
			return elements[indexElement];
		else
			return null;
	}

	public void addElement(XMLElement element)
	{
		if (element != null)
		{
			countElements++;
			if (countElements >= elements.length)
				setSizeElement(elements.length + 10);
			elements[countElements - 1] = element;
		}
	}

	public XMLElement findElement(String name)
	{
		if (name != null)
			for (int i = 0; i < countElements; i++)
				if (elements[i] != null)
				{
					if (elements[i].getName() != null && elements[i].getName().equals(name))
						return elements[i];
					XMLElement temp = elements[i].findElement(name);
					if (temp != null)
						return temp;
				}
		return null;
	}

	public boolean load(XMLStreamReader xml) throws XMLStreamException
	{
		// индификатор элемента
		switch (xml.next())
		{
			case XMLStreamConstants.START_ELEMENT:
				name = xml.getLocalName();
				countAttributes = xml.getAttributeCount();
				setSizeAttribut(countAttributes);
				for (int i = 0; i < attributes.length; i++)
					attributes[i] = new XMLAttribute(
							xml.getAttributeLocalName(i),
							xml.getAttributeValue(i));
				XMLElement element = new XMLElement();
				while (element.load(xml))
				{
					addElement(element);
					element = new XMLElement();
				}
				setSizeElement(countElements);
				return true;
			case XMLStreamConstants.END_ELEMENT:
				return false;
				//PROCESSING_INSTRUCTION=3;
			case XMLStreamConstants.CHARACTERS:
				value = xml.getText();
				return true;
				//COMMENT=5;
				/**
				 * The characters are white space
				 * (see [XML], 2.10 "White Space Handling").
				 * Events are only reported as SPACE if they are ignorable white
				 * space.  Otherwise they are reported as CHARACTERS.
				 * @see javax.xml.stream.events.Characters
				 */
				//SPACE=6;
				//START_DOCUMENT=7;
			case XMLStreamConstants.END_DOCUMENT:
				return false;
				/**
				 * Indicates an event is an entity reference
				 * @see javax.xml.stream.events.EntityReference
				 */
				//ENTITY_REFERENCE=9;
				//ATTRIBUTE=10;
				//DTD=11;
				//CDATA=12;
				//NAMESPACE=13;
				//NOTATION_DECLARATION=14;
				//ENTITY_DECLARATION=15;
		}
		return false;
	}

	public void save(XMLStreamWriter xml) throws XMLStreamException
	{
		if (name != null)
		{
			xml.writeStartElement(name);
			for (int i = 0; i < countAttributes; i++)
				attributes[i].save(xml);
			for (int i = 0; i < countElements; i++)
				elements[i].save(xml);
			xml.writeEndElement();
		} else
			xml.writeCharacters(value);
	}

	private void setSizeElement(int newLength)
	{
		elements = Arrays.copyOf(elements, newLength);
	}

	private void setSizeAttribut(int newLength)
	{
		attributes = Arrays.copyOf(attributes, newLength);
	}
}
