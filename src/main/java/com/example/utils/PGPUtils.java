package com.example.utils;

import java.io.*;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.openpgp.*;

public class PGPUtils {

//	@SuppressWarnings("unchecked")
//	public static void readPublicKey(String publicKeyFile) throws IOException, PGPException, InterruptedException {
//		List<String> command = new ArrayList<>();
//		command.add("gpg"); // GPG command
//		command.add("--import"); // Import key command
//		command.add(publicKeyFile); // Path to the public key file
//
//		// Create ProcessBuilder
//		ProcessBuilder processBuilder = new ProcessBuilder(command);
//		processBuilder.redirectErrorStream(true); // Merge stdout and stderr
//
//		// Start the process
//		Process process = processBuilder.start();
//
//		// Wait for the process to finish
//		int exitCode = process.waitFor();
//
//		// Check if the import was successful
//		if (exitCode == 0) {
//			System.out.println("Public key imported successfully.");
//		} else {
//			// Print the error output
//			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//				String line;
//				while ((line = reader.readLine()) != null) {
//					System.err.println(line);
//				}
//			}
//		}
//    }


	public static void encryptFile(String fileName, String outputFileName, String recipient)
			throws IOException, NoSuchProviderException, PGPException, InterruptedException {
		// Ensure the directory for the output file exists
		File outputFile = new File(outputFileName);
		File parentDir = outputFile.getParentFile();
		if (parentDir != null && !parentDir.exists()) {

			if (!parentDir.mkdirs()) {
				throw new IOException("Failed to create output directory: " + parentDir.getAbsolutePath());
			}
		}
		outputFile.delete();



		List<String> command = new ArrayList<>();
		command.add("gpg"); // GPG command
		command.add("--output"); // Specify output file
		command.add(outputFileName); // Encrypted file output
		command.add("--encrypt"); // Encrypt the file
		command.add("--recipient"); // Specify recipient's email (the key used for encryption)
		command.add(recipient); // Recipient's email or key ID (must match the key you imported)
		command.add(fileName); // File to be encrypted
		System.out.println("In encrypt file");

		// Create ProcessBuilder
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true); // Merge stdout and stderr

		// Start the process
		Process process = processBuilder.start();

		// Consume the process output asynchronously
		Thread outputReaderThread = new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line); // Log standard output
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		outputReaderThread.start();

		// Wait for the process to complete
		int exitCode = process.waitFor();

		// Ensure the output reader thread has completed
		outputReaderThread.join();

		// Check if the encryption was successful
		if (exitCode == 0) {
			System.out.println("File encrypted successfully. Output saved to: " + outputFileName);
		} else {
			System.err.println("Encryption failed with exit code: " + exitCode);
		}
	}



public static void decryptFile(String encryptFilePath, String decryptFilePath, String secretKeyFilePath, String passphrase)
		throws IOException, InterruptedException {
	// Command construction for GPG decryption
	List<String> command = new ArrayList<>();
	command.add("gpg");
	command.add("--batch"); // Non-interactive mode
	command.add("--yes"); // Overwrite output file if exists
	command.add("--passphrase");
	command.add(passphrase); // Passphrase for the private key
	command.add("--output");
	command.add(decryptFilePath); // Decrypted file output
	command.add("--decrypt");
//	command.add("--keyring");
//	command.add(secretKeyFilePath); // Path to the secret key
	command.add(encryptFilePath); // Path to the encrypted file

	System.out.println("Executing decryption command...");

	// Create ProcessBuilder
	ProcessBuilder processBuilder = new ProcessBuilder(command);
	processBuilder.redirectErrorStream(true); // Merge stdout and stderr

	// Start the process
	Process process = processBuilder.start();

	// Capture output for debugging
	try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
	}

	// Wait for the process to finish
	int exitCode = process.waitFor();

	if (exitCode == 0) {
		System.out.println("Decryption successful. Decrypted file: " + decryptFilePath);
	} else {
		throw new IOException("Decryption failed with exit code: " + exitCode);
	}
}

//	private static PGPPrivateKey findSecretKey(InputStream keyIn, long keyID, char[] pass)
//			throws IOException, PGPException, NoSuchProviderException {
//		PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
//				org.bouncycastle.openpgp.PGPUtil.getDecoderStream(keyIn));
//
//		PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);
//
//		if (pgpSecKey == null) {
//			return null;
//		}
//
//		return pgpSecKey.extractPrivateKey(pass, "BC");
//	}
//
//	/**
//	 * decrypt the passed in message stream
//	 * @throws IOException
//	 * @throws PGPException
//	 * @throws NoSuchProviderException
//	 */
//	@SuppressWarnings("unchecked")
//	public static void decryptFile(InputStream in, OutputStream out, InputStream keyIn, char[] passwd) throws IOException, NoSuchProviderException, PGPException {
//		Security.addProvider(new BouncyCastleProvider());
//
//		in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);
//
//		PGPObjectFactory pgpF = new PGPObjectFactory(in);
//		PGPEncryptedDataList enc;
//
//		Object o = pgpF.nextObject();
//		//
//		// the first object might be a PGP marker packet.
//		//
//		if (o instanceof PGPEncryptedDataList) {
//			enc = (PGPEncryptedDataList) o;
//		} else {
//			enc = (PGPEncryptedDataList) pgpF.nextObject();
//		}
//
//		//
//		// find the secret key
//		//
//		Iterator<PGPPublicKeyEncryptedData> it = enc.getEncryptedDataObjects();
//		PGPPrivateKey sKey = null;
//		PGPPublicKeyEncryptedData pbe = null;
//
//		while (sKey == null && it.hasNext()) {
//			pbe = it.next();
//
//			sKey = findSecretKey(keyIn, pbe.getKeyID(), passwd);
//		}
//
//		if (sKey == null) {
//			throw new IllegalArgumentException("Secret key for message not found.");
//		}
//
//		InputStream clear = pbe.getDataStream(sKey, "BC");
//
//		PGPObjectFactory plainFact = new PGPObjectFactory(clear);
//
//		Object message = plainFact.nextObject();
//
//		if (message instanceof PGPCompressedData) {
//			PGPCompressedData cData = (PGPCompressedData) message;
//			PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream());
//
//			message = pgpFact.nextObject();
//		}
//
//		if (message instanceof PGPLiteralData) {
//			PGPLiteralData ld = (PGPLiteralData) message;
//
//			InputStream unc = ld.getInputStream();
//			int ch;
//
//			while ((ch = unc.read()) >= 0) {
//				out.write(ch);
//			}
//		} else if (message instanceof PGPOnePassSignatureList) {
//			throw new PGPException("Encrypted message contains a signed message - not literal data.");
//		} else {
//			throw new PGPException("Message is not a simple encrypted file - type unknown.");
//		}
//
//		if (pbe.isIntegrityProtected()) {
//			if (!pbe.verify()) {
//				throw new PGPException("Message failed integrity check");
//			}
//		}
//	}


}