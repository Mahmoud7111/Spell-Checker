/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ecommerce.data;

import java.io.*;

/**
 *
 * @author asus
 */
public class FileManager {
//To write an object with ObjectOutputStream.writeObject, the object’s class must implement Serializable.
    
    // Save any serializable object to a file
    public static void saveToFile(String filePath, Object obj) {
        if (filePath == null) throw new IllegalArgumentException("filePath cannot be a NULL");
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {   //FileOutputStream(filePath) opens/creates the file for writing (overwrites by default).
                                    //ObjectOutputStream wraps the file stream 
                                   //and provides writeObject(...) to serialize an object graph.
            out.writeObject(obj);    //->Serializes obj and all referenced serializable objects reachable from it
        } 
        catch (IOException e) {
            System.err.println("Error saving to file: " + filePath);
            e.printStackTrace();  //prints the full error message + the line numbers where the error happened.
        }
    }

    // Load any serializable object from a file
    @SuppressWarnings("unchecked")  //tells the compiler: “I know there’s an unchecked cast below; don’t warn me about it.”
                                   //We need it because ObjectInputStream.readObject() returns Object and we cast (T) — compiler cannot verify it's safe at compile time.
    public static <T> T loadFromFile(String filePath, T defaultValue) {  // T:It means: “I don’t know the type yet — the caller of this method will specify it.” 
                                                                   //defaultValue is a convenient fallback if file doesn't exist or if loading fails. This avoids returning null unexpectedly                   
        File file = new File(filePath); // لازم نعمل file عشان ده مش outputstream
        if (!file.exists()) 
            return defaultValue; // return default if file missing (Don't open it)

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (T) in.readObject();  //Opens file and reads a serialized object.
                                        //readObject() returns Object, so we cast to T.
        } 
        catch (IOException | ClassNotFoundException e) { //happens if the class of the saved object isn't on the classpath
            System.err.println("Error loading from file: " + filePath);
            e.printStackTrace();
            return defaultValue;
        }
    }
}
