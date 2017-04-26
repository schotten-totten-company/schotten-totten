package com.boardgames.bastien.schotten_totten.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import com.boardgames.bastien.schotten_totten.model.Game;

import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class ByteArrayUtils {

	public ByteArrayUtils() {
		// TODO Auto-generated constructor stub
	}

	public static byte[] gameToByteArray(final Game g) throws IOException {
		try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			final ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(g);
			out.flush();
			return bos.toByteArray();
		}
	}

	public static ByteArrayEntity gameToByteArrayEntity(final Game g) throws IOException {
		return new ByteArrayEntity(gameToByteArray(g));
	}
	
	public static Game inputStreamToGame(final InputStream is) throws IOException {
		try (final ByteArrayInputStream bis = new ByteArrayInputStream(IOUtils.toByteArray(is))) {
			final ObjectInput in = new ObjectInputStream(bis);
			return (Game)in.readObject(); 
		} catch (ClassNotFoundException | IOException e) {
			throw new IOException(e.getCause());
		} 
	}
	
	public static ArrayList<String> inputStreamToSet(final InputStream is) throws IOException {
		try (final ByteArrayInputStream bis = new ByteArrayInputStream(IOUtils.toByteArray(is))) {
			final ObjectInput in = new ObjectInputStream(bis);
			return (ArrayList<String>)in.readObject(); 
		} catch (ClassNotFoundException | IOException e) {
			throw new IOException(e.getCause());
		} 
	}

		
}
