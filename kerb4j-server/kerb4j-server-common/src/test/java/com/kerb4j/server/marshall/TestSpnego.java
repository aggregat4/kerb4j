package com.kerb4j.server.marshall;

import com.kerb4j.common.util.SpnegoProvider;
import com.kerb4j.server.marshall.pac.Pac;
import com.kerb4j.server.marshall.pac.PacLogonInfo;
import com.kerb4j.server.marshall.pac.PacSid;
import com.kerb4j.server.marshall.spnego.SpnegoInitToken;
import com.kerb4j.server.marshall.spnego.SpnegoKerberosMechToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.security.auth.kerberos.KerberosKey;
import java.io.IOException;
import java.io.InputStream;

public class TestSpnego {

    private byte[] rc4Token;
    private byte[] desToken;
    private byte[] aes128Token;
    private byte[] aes256Token;
    private byte[] corruptToken;

    private KerberosKey aes256Keys[];


    @BeforeEach
    public void setUp() throws IOException {
        InputStream file;

        file = this.getClass().getClassLoader().getResourceAsStream("rc4-spnego-data");
        rc4Token = new byte[file.available()];
        file.read(rc4Token);
        file.close();

        file = this.getClass().getClassLoader().getResourceAsStream("des-spnego-data");
        desToken = new byte[file.available()];
        file.read(desToken);
        file.close();

        file = this.getClass().getClassLoader().getResourceAsStream("aes128-spnego-data");
        aes128Token = new byte[file.available()];
        file.read(aes128Token);
        file.close();

        file = this.getClass().getClassLoader().getResourceAsStream("aes256-spnego-data");
        aes256Token = new byte[file.available()];
        file.read(aes256Token);
        file.close();

        byte[] keyData;
        file = this.getClass().getClassLoader().getResourceAsStream("aes256-key-data");
        keyData = new byte[file.available()];
        file.read(keyData);
        aes256Keys = new KerberosKey[]{new KerberosKey(null, keyData, 18, 2)};
        file.close();

        corruptToken = new byte[]{5, 4, 2, 1};
    }

    @Test
    public void testRc4Token() {
        try {
            SpnegoInitToken spnegoToken = new SpnegoInitToken(rc4Token);

            Assertions.assertNotNull(spnegoToken);
            Assertions.assertTrue(spnegoToken instanceof SpnegoInitToken);
            Assertions.assertNotNull(spnegoToken.getMechToken());
            Assertions.assertTrue(spnegoToken.getMechToken().length < rc4Token.length);
            Assertions.assertNotNull(spnegoToken.getMechanism());
            Assertions.assertEquals(SpnegoProvider.LEGACY_KERBEROS_MECHANISM, spnegoToken.getMechanism());
        } catch (Kerb4JException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testDesToken() {
        try {
            SpnegoInitToken spnegoToken = new SpnegoInitToken(desToken);

            Assertions.assertNotNull(spnegoToken);
            Assertions.assertTrue(spnegoToken instanceof SpnegoInitToken);
            Assertions.assertNotNull(spnegoToken.getMechToken());
            Assertions.assertTrue(spnegoToken.getMechToken().length < desToken.length);
            Assertions.assertNotNull(spnegoToken.getMechanism());
            Assertions.assertEquals(SpnegoProvider.LEGACY_KERBEROS_MECHANISM, spnegoToken.getMechanism());
        } catch (Kerb4JException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAes128Token() {
        try {
            SpnegoInitToken spnegoToken = new SpnegoInitToken(aes128Token);

            Assertions.assertNotNull(spnegoToken);
            Assertions.assertTrue(spnegoToken instanceof SpnegoInitToken);
            Assertions.assertNotNull(spnegoToken.getMechToken());
            Assertions.assertTrue(spnegoToken.getMechToken().length < aes128Token.length);
            Assertions.assertNotNull(spnegoToken.getMechanism());
            Assertions.assertEquals(SpnegoProvider.LEGACY_KERBEROS_MECHANISM, spnegoToken.getMechanism());
        } catch (Kerb4JException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAes256Token() throws Kerb4JException {

        SpnegoInitToken spnegoToken = new SpnegoInitToken(aes256Token);

        Assertions.assertNotNull(spnegoToken);
        Assertions.assertTrue(spnegoToken instanceof SpnegoInitToken);
        Assertions.assertNotNull(spnegoToken.getMechToken());
        Assertions.assertTrue(spnegoToken.getMechToken().length < aes256Token.length);
        Assertions.assertNotNull(spnegoToken.getMechanism());
        Assertions.assertEquals(SpnegoProvider.LEGACY_KERBEROS_MECHANISM, spnegoToken.getMechanism());

        SpnegoKerberosMechToken token = spnegoToken.getSpnegoKerberosMechToken();

        try {

            Pac pac = token.getPac(aes256Keys);

            PacLogonInfo logonInfo = pac.getLogonInfo();
            Assertions.assertNotNull(logonInfo);
            Assertions.assertNotNull(logonInfo.getGroupSids());

            boolean found = false;

            for (PacSid groupSid : logonInfo.getGroupSids()) {
                if ("S-1-5-21-4028881986-3284141023-698984075-513".equals(groupSid.toHumanReadableString())) {
                    found = true;
                }
            }

            Assertions.assertTrue(found);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testEmptyToken() {
        SpnegoInitToken spnegoToken = null;
        try {
            spnegoToken = new SpnegoInitToken(new byte[0]);
            Assertions.fail("Should have thrown Kerb4JException.");
        } catch (Kerb4JException e) {
            Assertions.assertNotNull(e);
            Assertions.assertNull(spnegoToken);
        }
    }

    @Test
    public void testCorruptToken() {
        SpnegoInitToken spnegoToken = null;
        try {
            spnegoToken = new SpnegoInitToken(corruptToken);
            Assertions.fail("Should have thrown Kerb4JException.");
        } catch (Kerb4JException e) {
            Assertions.assertNotNull(e);
            Assertions.assertNull(spnegoToken);
        }
    }

    @Test
    public void testNullToken() {
        SpnegoInitToken spnegoToken = null;
        try {
            spnegoToken = new SpnegoInitToken(null);
            Assertions.fail("Should have thrown NullPointerException.");
        } catch (Kerb4JException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } catch (NullPointerException e) {
            Assertions.assertNotNull(e);
            Assertions.assertNull(spnegoToken);
        }
    }

}
