package com.example.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;

public interface PGPManager {

	public void encryptFile() throws NoSuchProviderException, IOException, PGPException;

	public void decryptFile()
			throws NoSuchProviderException, IOException, PGPException;

}
