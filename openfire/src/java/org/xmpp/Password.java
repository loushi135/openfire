package org.xmpp;

import org.jivesoftware.util.Blowfish;

public class Password {
	static Blowfish blowfish = new Blowfish("0ODqWpov94FitH4"); //passwordKey

	 

    public static void main(String[] args) {
        System.out.println(blowfish.decryptString("f76d2314ea872a6f0a49771d15de3d57f2a63ce754e0f82a0d931a6f82a5efb1")); //encryptedPassword
    }


}
