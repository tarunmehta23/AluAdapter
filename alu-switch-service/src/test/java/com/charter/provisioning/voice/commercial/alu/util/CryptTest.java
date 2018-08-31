package com.charter.provisioning.voice.commercial.alu.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CryptTest {

    @Test
    public void testCrypt_ExpectsCryptedString() throws Exception {
        String response = Crypt.encrypt();

        assertThat(response, is("*CHA*0u3J1L3s0W1P73740r3d3n123O163O7X6Y3c3d2f081K3M1Q2A0G3Z6X6V440U0@"));
    }

    @Test
    public void testDecrypt_ExpectsCryptedString() throws Exception {
        String response = Crypt.decrypt(Crypt.CHA + "0u3J1L3s0W1P73740r3d3n123O163O7X6Y3c3d2f081K3M1Q2A0G3Z6X6V440U0@");

        assertThat(response, is("1980"));
    }

}