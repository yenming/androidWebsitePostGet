package idv.ron.jsonex;

import idv.ron.vo.Book;
import idv.ron.vo.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONEx {
	public static void main(String[] args) throws JSONException, ParseException {
		String jsonStr = "";

		// Data for testing
		Book book1 = new Book("Java", 500, "John");
		Book book2 = new Book("Android", 600, "Allen");
		List<Book> bookList = new ArrayList<Book>();
		bookList.add(book1);
		bookList.add(book2);
		Order order = new Order("111", "ron", new Date(), bookList);

		// Object to JSON
		jsonStr = new JSONObject(book1).toString();
		System.out.println("Object to JSON: " + jsonStr);
		// JSON to Object
		JSONObject jsonObj = new JSONObject(jsonStr);
		String name = jsonObj.getString("name");
		double price = jsonObj.getDouble("price");
		String author = jsonObj.getString("author");
		Book myBook = new Book(name, price, author);
		myBook.show();
		System.out.println();

		// List to JSON
		jsonStr = new JSONArray(bookList).toString();
		System.out.println("List to JSON: " + jsonStr);
		// JSON to List
		List<Book> books = new ArrayList<Book>();
		JSONArray jsonArray = new JSONArray(jsonStr);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject json_book = jsonArray.getJSONObject(i);
			String book_name = json_book.getString("name");
			double book_price = json_book.getDouble("price");
			String book_author = json_book.getString("author");
			Book book = new Book(book_name, book_price, book_author);
			books.add((book));
		}
		for (Book book : books) {
			book.show();
		}
		System.out.println();

		// Object (with List) to JSON
		jsonStr = new JSONObject(order).toString();
		System.out.println("Object (with List) to JSON: " + jsonStr);
		// JSON to Object (with List)
		JSONObject orderObj = new JSONObject(jsonStr);
		String orderId = orderObj.getString("orderId");
		String customer = orderObj.getString("customer");
		// Locale.ENGLISH could be needed if current locale != ENGLISH 
		// pattern letters refers to SimpleDateFormat in Javadoc
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
		Date date = simpleDateFormat.parse(orderObj.getString("date"));
		JSONArray jsonArray_books = orderObj.getJSONArray("bookList");
		List<Book> myBookList = new ArrayList<Book>();
		for (int i = 0; i < jsonArray_books.length(); i++) {
			JSONObject json_book = jsonArray_books.getJSONObject(i);
			String bookName = json_book.getString("name");
			double bookPrice = json_book.getDouble("price");
			String bookAuthor = json_book.getString("author");
			Book book = new Book(bookName, bookPrice, bookAuthor);
			myBookList.add((book));
		}
		Order myOrder = new Order(orderId, customer, date, myBookList);
		myOrder.show();
	}
}
