package idv.ron.gsonex;

import idv.ron.vo.Book;
import idv.ron.vo.Order;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class GsonEx {
	public static void main(String[] args) {
		Gson gson = new Gson();
		String jsonStr = "";

		// Data for testing
		Book book1 = new Book("Java", 500, "John");
		Book book2 = new Book("Android", 600, "Allen");
		List<Book> bookList = new ArrayList<Book>();
		bookList.add(book1);
		bookList.add(book2);
		Order order = new Order("111", "ron", new Date(), bookList);

		// Object to JSON
		jsonStr = gson.toJson(book1);
		System.out.println("Object to JSON: " + jsonStr);
		// JSON to Object
		System.out.println("JSON to Object: ");
		Book myBook = gson.fromJson(jsonStr, Book.class);
		myBook.show();
		// get JsonObject's value from JSON
		System.out.println("get JsonObject's value from JSON: ");
		JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
		String name = jsonObject.get("name").getAsString();
		double price = jsonObject.get("price").getAsDouble();
		String author = jsonObject.get("author").getAsString();
		new Book(name, price, author).show();
		System.out.println();

		// List to JSON
		jsonStr = gson.toJson(bookList);
		System.out.println("List to JSON: " + jsonStr);
		// JSON to List
		System.out.println("JSON to List: ");
		/*
		 * TypeToken represents a generic type. TypeToken is an abstract class
		 * but with no abstract methods, thus we don't have to override any
		 * method in TypeToken.
		 */
		Type collectionType = new TypeToken<List<Book>>() {
		}.getType();
		List<Book> myBookList = gson.fromJson(jsonStr, collectionType);
		for (Book book : myBookList) {
			book.show();
		}
		// get JsonArray's value from JSON
		System.out.println("get JsonArray's value from JSON: ");
		JsonArray jsonArray = gson.fromJson(jsonStr, JsonArray.class);
		for (JsonElement element : jsonArray) {
			JsonObject obj = element.getAsJsonObject();
			name = obj.get("name").getAsString();
			price = obj.get("price").getAsDouble();
			author = obj.get("author").getAsString();
			new Book(name, price, author).show();
		}
		System.out.println();
		
		// Object (with List) to JSON
		jsonStr = gson.toJson(order);
		System.out.println("Object (with List) to JSON: " + jsonStr);
		// JSON to Object (with List)
		Order myOrder = gson.fromJson(jsonStr, Order.class);
		myOrder.show();

	}
}
