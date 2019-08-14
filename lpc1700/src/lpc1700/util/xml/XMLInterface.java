package lpc1700.util.xml;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 16.11.2007
 * Time: 7:37:27
 * TODO.
 */
public interface XMLInterface
{
	// объект заполняет структру XMLElement
	//!!public XMLElement getElement();

	// объект считывает данные из XMLElement

	public void readFromXMLElement(XMLElement element);

	public XMLElement writeToXMLElement(String name);
}