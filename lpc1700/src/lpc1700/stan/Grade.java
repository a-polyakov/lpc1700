package lpc1700.stan;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 22.10.2007
 * Time: 7:55:39
 * Сорт
 * - неизвестны необходимые параметрй(теплоемкость, эластичность)
 */
public class Grade
{
	public String name;
	public float q;
	public float t;

	public Grade(String name)
	{
		this.name = name;
	}

	public boolean equals(Object o)
	{
		return o.getClass() == Grade.class && name.equals(((Grade) o).name);
	}
}
