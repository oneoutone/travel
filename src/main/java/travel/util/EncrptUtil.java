package travel.util;

import com.mysql.cj.util.Base64Decoder;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64Encoder;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class EncrptUtil {

     static final private String key= "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOX8aJvbB1LNbuf7\n" +
             "OkHvq2R8duOLwcyIb/24hUOf6X0fIepjda5wRzDsNft6gJjdLcTq+otg7O+sCKkX\n" +
             "uZxYrtyHrsHoZDSGIzAfHM5C8bc93fDS3urquYEzeXn14E0xs9naVnU27K5kyBw4\n" +
             "8st2fcYQ3IQ1SQxKu4CICUkg5zgPAgMBAAECgYEAs+UW8mqGNPeqO90gebj+KLDO\n" +
             "0jhclB9Y8m6mFvp7ybpTbiL0R0ld9l+d7iKpemjvxytCZUmgq+y2LYX4Utpvk2bN\n" +
             "Ivsd4AQlYoQ8SNv3EPXEB8TR8HqdFq2bIcmpOw2KbGkbP0+GkPm2niFWQ+3QAVH1\n" +
             "37Wn8FFR7o1zk5YYJIECQQD8cJxD3p3fOrT6jXGH1s4MGZy3+GhFn/epJE/PTBSJ\n" +
             "u2BhiZMhouKIQltQVALxY/Zkbaz8C2ptmRe7OjYkdnBPAkEA6Tq7eh6mRIWt5bLH\n" +
             "Nq71iPmCqyT3jF4KoMfvu84mxjdMJR64aRsSQiZDB17UpQWZtcukpzZSR8q7nweR\n" +
             "yB8MQQJBALwn81FjDKjmSR63bnehU9MRBA7byFyc7yvTNOl9+5DtaNlKgbAAiHin\n" +
             "fQhSlh72MyCH3ve+SoWGJnoW2WQ0I2ECQQDBpilpsS8eb9w+fx1XcxedfoYXMh90\n" +
             "ZdTvQHL1/apGJD3OCF7XrkmPsRMweHWY+zhwMMDqsXjJ8TOtTGynu9fBAkA/tKHV\n" +
             "lSvxTldtja0yhz1044zeHjX4d2fZYkPp1kS9dHEljYhL3LpE0tt0I58CSFKqXMIr\n" +
             "dS6WYNBUQCUU0o7m";

     static public String decrypt(String plainTextData) throws Exception {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            Cipher cipher = null;
            // 使用默认RSA
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(Base64.decodeBase64(plainTextData));
            return new String(output);
    }
}
