package idv.ron.vo;

public class Book {
	private String name;
	private double price;
	private String author;

	public Book() {
		super();
	}

	public Book(String name, double price, String author) {
		super();
		this.name = name;
		this.price = price;
		this.author = author;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void show() {
		System.out.println("name = " + name + "; price = " + price
				+ "; author = " + author);
	}
}
