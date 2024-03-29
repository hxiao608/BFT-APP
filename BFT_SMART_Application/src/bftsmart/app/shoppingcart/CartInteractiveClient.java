/**
Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
 * @Editor; Haoru
 * @description: the User Interface for manipulating the database
 */

package bftsmart.app.shoppingcart;

import java.io.IOException;
import java.util.Scanner;
import java.util.Map;
import java.util.InputMismatchException;

import java.io.Console;
import java.util.TreeMap;

public class CartInteractiveClient {


	public static void main(String[] args) throws IOException {
		if(args.length < 1) {
			System.out.println("Usage: java CartInteractiveClient <process id>");
			System.exit(-1);
		}

		Cart cart = new Cart(Integer.parseInt(args[0]));
//		System.out.println(cart.test());
		Console console = System.console();
		Scanner sc = new Scanner(System.in);


		while(true) {
			System.out.println("---------------Select one of the commands ----------------");
			System.out.println("select a command : 0. GENERATE SHOPPING CART TABLE");
			System.out.println("select a command : 1. CREATE A NEW SHOPPING CART");
			System.out.println("select a command : 2. REMOVE AN EXISTING SHOPPING CART");
			System.out.println("select a command : 3. GET THE COUNT OF ALL SHOPPING CARTS");
			System.out.println("select a command : 4. PUT PRODUCT INTO A SHOPPING CART");
			System.out.println("select a command : 5. GET PRODUCT FROM A SHOPPING CART"); // can be replaced by command 8
			System.out.println("select a command : 6. GET THE SIZE OF A SHOPPING CART");
			System.out.println("select a command : 7. REMOVE PRODUCT FROM A SHOPPING CART");
			System.out.println("select a command : 8. READ CONTENT OF A SHOPPING CART");
			System.out.println("select a command : 9. EXIT");
			System.out.println("select a command : 10. Initialize Database");
			System.out.println("----------------------------------------------------------");

			int cmd = sc.nextInt();
			boolean continueInput = true;
			String cartName;
			boolean cartExists;
			switch(cmd) {
			case 10:
				cart.dataInitialize(); //initalize data
				break;
			case CartRequestType.READALL:
				int sizeDB = cart.size();
				if (sizeDB <= 0) {
					// if not cart exists in the database
					System.out.println("Database is empty");
				}else{ // if there exist at least one cart in the database, print the content of the database;
//						System.out.println("here");
					Map<String, Map<String,byte[]>> db = cart.readAll();
					System.out.println("Database Size: "+ sizeDB);
					System.out.println("content of the database:");
					System.out.println("-------------------------------------------------");
					for(String k1 : db.keySet()) {
						Map<String, byte[]> tmp = cart.read(k1);
						System.out.println(">>> Cart: " + k1);
						for (String k2 : tmp.keySet()) {
							System.out.println("product key: " + k2 + ", quantity: " + new String(tmp.get(k2)));
						}
						System.out.println();
					}
					// TODO printout the layout of the database;
//					for(String k: res.keySet()){
//						System.out.println("key: "+k + ", value: "+new String(res.get(k)));
//					}
					System.out.println("-------------------------------------------------");
				}
				break;
			case CartRequestType.READ:
				cartExists = false;
				do {
					cartName = console.readLine("Enter the Cart name: ");
					cartExists = cart.containsKey(cartName);
					System.out.println("cart name: "+ cartName);
					if (!cartExists) {
						//if the table name does not exist then print the error message
						System.out.println("cart does not exists");
					}else{
//						System.out.println("here");
						Map<String,byte[]> res = cart.read(cartName);
						System.out.println("-------------------------------------------");
						System.out.println("cart content of "+ cartName);
						for(String k: res.keySet()){
							System.out.println("product key: "+ k + ", quantity: "+new String(res.get(k)));
						}
						System.out.println("-------------------------------------------");
						cartExists = true;
					}
				} while(!cartExists);
				break;
			case CartRequestType.CART_REMOVE:
				//Remove the table entry
				cartExists = false;
				cartName = null;
				System.out.println("Removing cart");
				cartName = console.readLine("Enter the valid cart name you want to remove: ");
				cartExists = cart.containsKey(cartName);
				if(cartExists) {
					Map<String,byte[]> map = cart.remove(cartName);

					System.out.println("Cart removed: " + map.keySet());
				} else
					System.out.println("Cart not found");
				break;
				//operations on the table
			case CartRequestType.CART_CREATE:
				cartExists = false;
				do {
					cartName = console.readLine("Enter the Cart name: ");
					cartExists = cart.containsKey(cartName);
					if (!cartExists) {
						//if the table name does not exist then create the table
						cart.put(cartName, new TreeMap<String,byte[]>());
					}else{
						System.out.println("Cart already exists");
					}
				} while(cartExists);
				break;

			case CartRequestType.SIZE_CART:
				//obtain the size of the carts of database.
				System.out.println("Computing the size of the database");
				int size = cart.size();
				System.out.println("The size of the database is: "+size);
				break;

				//operations on the hashmap
			case CartRequestType.PUT:
				System.out.println("Execute put function");
				cartExists = false;
				cartName = null;
				size = -1;
				cartName = console.readLine("Enter the valid cart name in which you want to insert data: ");
				String key = console.readLine("Enter a numeric key for the new product in the range 0 to 9999: ");
				String value = console.readLine("Enter the quantity for the new product: ");

				byte[] resultBytes;
				cartExists = cart.containsKey(cartName);
				if(cartExists) {
					while(key.length() < 4)
						key = "0" + key;
					byte[] byteArray = value.getBytes();
					resultBytes = cart.putEntry(cartName, key, byteArray);
				} else
					System.out.println("Cart not found");
				break;

			case CartRequestType.GET:
				cartExists = false;
				boolean keyExists = false;
				cartName = null;
				key = null;
				cartName = console.readLine("Enter the valid cart name from which you want to get the quantity of product: ");
				cartExists = cart.containsKey(cartName);
				if (cartExists) {
					key = console.readLine("Enter the key of the product: ");
					while(key.length() < 4)
						key = "0" + key;
					keyExists = cart.containsKey1(cartName, key);
					if(keyExists) {
						resultBytes = cart.getEntry(cartName,key);
						System.out.println("The quantity of the product you request is: " + new String(resultBytes));
					} else
						System.out.println("product key not found");
				} else
					System.out.println("Cart not found");
				break;

			case CartRequestType.SIZE:
				cartExists = false;
				cartName = null;
				size = -1;
				cartName = console.readLine("Enter the valid cart whose size you want to determine: ");
				cartExists = cart.containsKey(cartName);
				if (cartExists) {
					size = cart.size1(cartName);
					System.out.println("The size is: " + size);
				} else {
					System.out.println("Cart not found");
				}
				break;
			case CartRequestType.REMOVE:
				cartExists = false;
				keyExists = false;
				cartName = null;
				key = null;
				cartName = console.readLine("Enter the cart name from which you want to remove: ");
				cartExists = cart.containsKey(cartName);
				if(cartExists) {
					key = console.readLine("Enter the valid product id: ");
					keyExists = cart.containsKey1(cartName, key);
					if(keyExists) {
						byte[] result2 = cart.removeEntry(cartName,key);
						System.out.println("The previous quantity of the product was : "+new String(result2));
					} else
						System.out.println("Product key not found");
				} else
					System.out.println("Cart not found");
				break;
			case CartRequestType.EXIT:
				System.exit(-1);
				break;
			default:
				System.out.println("Invalid Input, try again");
			}
		}
	}
}
