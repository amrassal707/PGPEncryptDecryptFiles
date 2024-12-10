package com.example.manager;

import com.example.configurations.FilesConfigProperties;
import com.example.configurations.PGPConfigProperties;
import com.example.utils.PGPUtils;
import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;


@Service
public class PGPManagerImpl implements PGPManager {

    private static Logger logger = LoggerFactory.getLogger(PGPManagerImpl.class);

    private FilesConfigProperties filesConfigProperties;

    private PGPConfigProperties pgpConfigProperties;

    public PGPManagerImpl(FilesConfigProperties filesConfigProperties, PGPConfigProperties pgpConfigProperties) {

        this.filesConfigProperties = filesConfigProperties;
        this.pgpConfigProperties = pgpConfigProperties;

    }

    @Override
    public void encryptFile() throws NoSuchProviderException, IOException, PGPException {
        logger.info("encryptFile()");
        PGPUtils.encryptFile(new FileOutputStream(filesConfigProperties.getEncryptFilePath()), filesConfigProperties.getOriginalFilePath(), PGPUtils.readPublicKey(new FileInputStream(pgpConfigProperties.getPublicKeyFilePath())), pgpConfigProperties.isAsciiArmored(), pgpConfigProperties.isIntegrityCheck());


    }

    @Override
    public void decryptFile() throws NoSuchProviderException, IOException, PGPException {

        logger.info("decryptFile()");

        PGPUtils.decryptFile(new FileInputStream(filesConfigProperties.getEncryptFilePath()), new FileOutputStream(filesConfigProperties.getDecryptFilePath()), new FileInputStream(pgpConfigProperties.getSecretKeyFilePath()), pgpConfigProperties.getPassphrase().toCharArray());
    }

}
