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
package bftsmart.app.shoppingcart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;
import java.util.Map;

import bftsmart.tom.ServiceProxy;

/**
 * @Editor; Haoru, Yao
 * @description: implement functions provided by the Cart Object
 */
public class Cart implements Map<String, Map<String,byte[]>> {

	ServiceProxy KVProxy = null;

	public Cart(int id) {
		KVProxy = new ServiceProxy(id, "config");
	}
	ByteArrayOutputStream out = null;

//	public String test(){
//		return "yes!";
//	}

	String[] cartNames = new String[] {"shoes", "clothes", "electronics", "makeups", "games", "pets", "food"};
	String[] keySet = new String[]{"0001", "0002", "0003", "0004", "0005"};
	public void dataInitialize(){
		Random random = new Random();
		for(int i = 0; i<cartNames.length; i++){
			put(cartNames[i], new TreeMap<String,byte[]>()); //Create cart
			for(int j = 0; j<keySet.length; j++){
				putEntry(cartNames[i], keySet[j], Integer.toString(random.nextInt(101)).getBytes()); //put value
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Map<String,byte[]>> readAll() {
		try {
			out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(CartRequestType.READALL);
			byte[] rep = KVProxy.invokeOrdered(out.toByteArray());
//			System.out.println("rep from server: "+ new String(rep));
			ByteArrayInputStream bis = new ByteArrayInputStream(rep) ;
			ObjectInputStream in = new ObjectInputStream(bis) ;
			Map<String, Map<String,byte[]>> db = (Map<String, Map<String,byte[]>>) in.readObject();
			in.close();
			return db;
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		} catch (IOException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public Map<String,byte[]> read(String cartName) {
		try {
			out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(CartRequestType.READ);
			dos.writeUTF(cartName);
			byte[] rep = KVProxy.invokeOrdered(out.toByteArray());
//			System.out.println("rep from server: "+ new String(rep));
			ByteArrayInputStream bis = new ByteArrayInputStream(rep) ;
			ObjectInputStream in = new ObjectInputStream(bis) ;
			Map<String,byte[]> content = (Map<String,byte[]>) in.readObject();
			in.close();
			return content;
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		} catch (IOException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public byte[] getEntry(String cartName,String key) {
		try {
			out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(CartRequestType.GET);
			dos.writeUTF(cartName);
			dos.writeUTF(key);
			byte[] rep = KVProxy.invokeUnordered(out.toByteArray());
			return rep;
		} catch (IOException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String,byte[]> put(String key, Map<String,byte[]> value) {
		try {
			out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(CartRequestType.CART_CREATE);
			dos.writeUTF(key);
			//ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
			ObjectOutputStream  out1 = new ObjectOutputStream(out) ;
			out1.writeObject(value);
			out1.close();
			byte[] rep = KVProxy.invokeOrdered(out.toByteArray());
			ByteArrayInputStream bis = new ByteArrayInputStream(rep) ;
			ObjectInputStream in = new ObjectInputStream(bis) ;
			Map<String,byte[]> cart = (Map<String,byte[]>) in.readObject();
			in.close();
			return cart;

		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		} catch (IOException ex) {
			ex.printStackTrace();
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public byte[] putEntry(String cartName, String key, byte[] value) {
		try {
			out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(CartRequestType.PUT);
			dos.writeUTF(cartName);
			dos.writeUTF(key);
			dos.writeUTF(new String(value));
			byte[] rep = KVProxy.invokeOrdered(out.toByteArray());
			return rep;
		} catch (IOException ex) {
			ex.printStackTrace();
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public Map<String,byte[]> remove(Object key) {
		try {
			out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(CartRequestType.CART_REMOVE);
			dos.writeUTF((String) key);
			byte[] rep = KVProxy.invokeOrdered(out.toByteArray());
			System.out.println("remove object rep: "+new String(rep)); //test
			ByteArrayInputStream bis = new ByteArrayInputStream(rep) ;
			ObjectInputStream in = new ObjectInputStream(bis) ;
			Map<String,byte[]> cart = (Map<String,byte[]>) in.readObject();
			in.close();
			return cart;
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		} catch (IOException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}

	}

	public byte[] removeEntry(String cartName,String key)  {
		try {
			out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(CartRequestType.REMOVE);
			dos.writeUTF((String) cartName);
			dos.writeUTF((String) key);
			byte[] rep = KVProxy.invokeOrdered(out.toByteArray());
			return rep;
		} catch (IOException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}

	}
	public int size() {
		try {
			out = new ByteArrayOutputStream();
			new DataOutputStream(out).writeInt(CartRequestType.SIZE_CART);
			byte[] rep;
			rep = KVProxy.invokeUnordered(out.toByteArray());
			ByteArrayInputStream in = new ByteArrayInputStream(rep);
			int size = new DataInputStream(in).readInt();
			return size;
		} catch (IOException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return -1;
		}
	}

	public int size1(String cartName) {
		try {
			out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(CartRequestType.SIZE);
			dos.writeUTF(cartName);
			byte[] rep;
			rep = KVProxy.invokeUnordered(out.toByteArray());
			ByteArrayInputStream in = new ByteArrayInputStream(rep);
			int size = new DataInputStream(in).readInt();
			return size;
		} catch (IOException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return 0;
		}
	}

	public boolean containsKey(String key) {
		try {
			out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(CartRequestType.CART_CREATE_CHECK);
			dos.writeUTF((String) key);
			byte[] rep; // Exception: rep is null
			rep = KVProxy.invokeUnordered(out.toByteArray());
			ByteArrayInputStream in = new ByteArrayInputStream(rep);
			boolean res = new DataInputStream(in).readBoolean();
			return res;
		} catch (IOException ex) {
			ex.printStackTrace();
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}

	}

	public boolean containsKey1(String cartName, String key) {
		try {
			out = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(out);
			dos.writeInt(CartRequestType.CHECK);
			dos.writeUTF((String) cartName);
			dos.writeUTF((String) key);
			byte[] rep;
			rep = KVProxy.invokeUnordered(out.toByteArray());
			ByteArrayInputStream in = new ByteArrayInputStream(rep);
			boolean res = new DataInputStream(in).readBoolean();
			return res;
		} catch (IOException ex) {
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}




	public boolean isEmpty() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void putAll(Map m) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void clear() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Set keySet() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Collection values() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Set entrySet() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean containsKey(Object key) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public TreeMap<String, byte[]> get(Object key) {
		System.out.println("not supported");
		throw new UnsupportedOperationException("Not supported yet.");
	}

}