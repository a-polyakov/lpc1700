package lpc1700.util.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 15.11.2007
 * Time: 14:35:23
 * Страница данных в xml формате
 */
public class XMLPage {
    private String version;            // версия
    private String encoding;        // кодировка
    private XMLElement elements[];    // массив элементов
    private int countElements;        // количество элементов

    public XMLPage() {
        version = "1.0";
        encoding = "UTF-8";
        elements = new XMLElement[10];
        countElements = 0;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public XMLElement[] getElements() {
        return Arrays.copyOf(elements, countElements);
    }

    public int getCountElements() {
        return countElements;
    }

    public XMLElement getElement(int indexElement) {
        if (indexElement >= 0 && indexElement < countElements)
            return elements[indexElement];
        else
            return null;
    }

    public void setElement(XMLElement element, int indexElement) {
        if (element != null && indexElement >= 0 && indexElement < countElements)
            elements[indexElement] = element;
    }

    public void addElement(XMLElement element) {
        if (element != null) {
            countElements++;
            if (countElements >= elements.length)
                setSizeElement(elements.length + 10);
            elements[countElements - 1] = element;
        }
    }

    public XMLElement findElement(String name) {
        if (name != null)
            for (int i = 0; i < countElements; i++)
                if (elements[i] != null) {
                    if (elements[i].getName() != null && elements[i].getName().equals(name))
                        return elements[i];
                    XMLElement temp = elements[i].findElement(name);
                    if (temp != null)
                        return temp;
                }
        return null;
    }

    private void setSizeElement(int newLength) {
        elements = Arrays.copyOf(elements, newLength);
    }

    public void load(File file) {
        try {
            InputStream in = new FileInputStream(file);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader xml = factory.createXMLStreamReader(in);
            encoding = xml.getEncoding();
            version = xml.getVersion();
            XMLElement element = new XMLElement();
            while (element.load(xml)) {
                addElement(element);
                element = new XMLElement();
            }
            setSizeElement(countElements);
            xml.close();
            in.close();
        }
        catch (IOException e) {
        }
        catch (XMLStreamException e) {
        }
    }

    public void save(File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xml = factory.createXMLStreamWriter(out);
            xml.writeStartDocument(encoding, version);
            for (int i = 0; i < countElements; i++)
                elements[i].save(xml);
            xml.writeEndDocument();
            xml.close();
            out.close();
        }
        catch (IOException e) {
        }
        catch (XMLStreamException e) {
        }
    }

    public static void main(String arg[]) {
        XMLPage page = new XMLPage();
        page.load(new File("data/stan.xml"));
        page.save(new File("data/test.xml"));
    }

}
