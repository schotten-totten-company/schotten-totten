package com.boardgames.bastien.schotten_totten.server;

import com.boardgames.bastien.schotten_totten.controllers.AbstractGameManager;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class ByteArrayUtils {

	public ByteArrayUtils() {
		// TODO Auto-generated constructor stub
	}

	public static byte[] gameToByteArray(final AbstractGameManager g) throws IOException {
		try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			final ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(g);
			out.flush();
			return bos.toByteArray();
		}
	}

	public static ByteArrayEntity gameToByteArrayEntity(final AbstractGameManager g) throws IOException {
		return new ByteArrayEntity(gameToByteArray(g));
	}
	
	public static AbstractGameManager inputStreamToGame(final InputStream is) throws IOException, ClassNotFoundException {
		try (final ByteArrayInputStream bis = new ByteArrayInputStream(IOUtils.toByteArray(is))) {
			final ObjectInput in = new ObjectInputStream(bis);
			return (AbstractGameManager)in.readObject();
		}
	}

	public static AbstractGameManager byteArrayToGame(final byte[] bArray) throws IOException, ClassNotFoundException {
		try (final ByteArrayInputStream bis = new ByteArrayInputStream(bArray)) {
			final ObjectInput in = new ObjectInputStream(bis);
			return (AbstractGameManager)in.readObject();
		}
	}
	
	public static ArrayList<String> inputStreamToSet(final InputStream is) throws IOException, ClassNotFoundException {
		try (final ByteArrayInputStream bis = new ByteArrayInputStream(IOUtils.toByteArray(is))) {
			final ObjectInput in = new ObjectInputStream(bis);
			return (ArrayList<String>)in.readObject();
		} 
	}

		
}
