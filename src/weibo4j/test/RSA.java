package weibo4j.test;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;



public class RSA {
	/** 指定key的大小 */
	private static int KEYSIZE = 1024;

	
	 public static void main(String[] args){
		 String pubkey = "PublicKey(165138424261149263963666229661164814908887524950166142962960019363944425161240370251403452452001165143400173133423045791330687304650944332950460079059702342999940532642226896299225258939028313437520982527474148958262129523279095471616009516621824844891755906467794220597075349492626446841979774101805104112707, 65537)";
		 KEYSIZE = pubkey.length();
		 String message = "1432237670" + "\t" + "NDSPMB" + "\n" + "student2001";
		 try {
			String result = encryptToBase64(message, pubkey);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	 }
	
	
	/**
	 * 生成RSA密钥对
	 * 
	 * @return 返回密钥组，Map<String,
	 *         String>类型。其中publicKey为公钥，privateKey为私钥，modulus为模
	 * @throws Exception
	 */
	public static Map<String, String> generateKeyPair() throws Exception {
		/** RSA算法要求有一个可信任的随机数源 */
		SecureRandom sr = new SecureRandom();
		/** 为RSA算法创建一个KeyPairGenerator对象 */
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		/** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
		kpg.initialize(KEYSIZE, sr);
		/** 生成密匙对 */
		KeyPair kp = kpg.generateKeyPair();
		/** 得到公钥 */
		Key publicKey = kp.getPublic();
		byte[] publicKeyBytes = publicKey.getEncoded();
		String pub = new String(Base64.encodeBase64(publicKeyBytes),
				ConfigureEncryptAndDecrypt.CHAR_ENCODING);
		/** 得到私钥 */
		Key privateKey = kp.getPrivate();
		byte[] privateKeyBytes = privateKey.getEncoded();
		String pri = new String(Base64.encodeBase64(privateKeyBytes),
				ConfigureEncryptAndDecrypt.CHAR_ENCODING);

		Map<String, String> map = new HashMap<String, String>();
		map.put("publicKey", pub);
		map.put("privateKey", pri);
		RSAPublicKey rsp = (RSAPublicKey) kp.getPublic();
		BigInteger bint = rsp.getModulus();
		byte[] b = bint.toByteArray();
		byte[] deBase64Value = Base64.encodeBase64(b);
		String retValue = new String(deBase64Value);
		map.put("modulus", retValue);
		return map;
	}

	/**
	 * RSA加密方法
	 * 
	 * @param source
	 *            源数据，加密前明文
	 * @param publicKey
	 *            字符型公钥
	 * @return Base64转换后的加密结果
	 * @throws Exception
	 */
	public static String encryptToBase64(String source, String publicKey)
			throws Exception {
		return encryptToBase64(source, publicKey,
				ConfigureEncryptAndDecrypt.CHAR_ENCODING);
	}

	/**
	 * RSA加密方法
	 * 
	 * @param source
	 *            源数据，加密前明文
	 * @param publicKey
	 *            字符型公钥
	 * @return Base64转换后的加密结果
	 * @throws Exception
	 */
	public static String encryptToBase64(String source, String publicKey,
			String charset) throws Exception {
		PublicKey key = getPublicKey(publicKey, charset);
		return encryptToBase64(source, key, charset);
	}

	/**
	 * RSA加密方法
	 * 
	 * @param source
	 *            源数据，加密前明文
	 * @param key
	 *            公钥
	 * @return Base64转换后的加密结果
	 * @throws Exception
	 */
	public static String encryptToBase64(String source, PublicKey key)
			throws Exception {
		return encryptToBase64(source, key,
				ConfigureEncryptAndDecrypt.CHAR_ENCODING);
	}

	/**
	 * 
	 * RSA加密方法
	 * 
	 * @param source
	 *            源数据，加密前明文
	 * @param key
	 *            公钥
	 * @param charset
	 *            加密明文字符集类型
	 * @return
	 * @throws Exception
	 */
	public static String encryptToBase64(String source, PublicKey key,
			String charset) throws Exception {
		byte[] sourceByte = source.getBytes(charset);
		/** 执行加密操作 */
		byte[] b1 = encrypt(sourceByte, key);
		return new String(Base64.encodeBase64(b1), charset);
	}

	/**
	 * RSA加密方法
	 * 
	 * @param source
	 *            待加密内容字节数组
	 * @param key
	 *            加密用公钥
	 * @return 加密后的byte数组
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] source, PublicKey key) throws Exception {
		/** 得到Cipher对象来实现对源数据的RSA加密 */
		Cipher cipher = Cipher
				.getInstance(ConfigureEncryptAndDecrypt.RSA_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		/** 执行加密操作 */
		byte[] result = cipher.doFinal(source);
		return result;
	}

	/**
	 * RSA解密算法
	 * 
	 * @param cryptograph
	 *            Base64格式的密文
	 * @param privateKey
	 *            字符型私钥
	 * @return
	 * @throws Exception
	 */
	public static String decryptFromBase64(String cryptograph, String privateKey)
			throws Exception {
		return decryptFromBase64(cryptograph, privateKey,
				ConfigureEncryptAndDecrypt.CHAR_ENCODING);
	}

	/**
	 * 解密算法
	 * 
	 * @param cryptograph
	 *            Base64格式的密文
	 * @param privateKey
	 *            字符型私钥
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String decryptFromBase64(String cryptograph,
			String privateKey, String charset) throws Exception {
		PrivateKey key = getPrivateKey(privateKey, charset);
		return decryptFromBase64(cryptograph, key, charset);
	}

	/**
	 * RSA解密方法 默认使用UTF-8编码
	 * 
	 * @param cryptograph
	 *            Base64格式的密文
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decryptFromBase64(String cryptograph, PrivateKey key)
			throws Exception {
		return decryptFromBase64(cryptograph, key,
				ConfigureEncryptAndDecrypt.CHAR_ENCODING);
	}

	/**
	 * RSA解密方法
	 * 
	 * @param cryptograph
	 *            需要解密的密文
	 * @param key
	 *            解密用的私钥
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String decryptFromBase64(String cryptograph, PrivateKey key,
			String charset) throws Exception {
		byte[] b1 = Base64.decodeBase64(cryptograph.getBytes(charset));
		byte[] b = decrypt(b1, key);
		return new String(b, charset);
	}

	/**
	 * RSA解密方法
	 * 
	 * @param cryptograph
	 *            需要解密的密文
	 * @param key
	 *            解密用的私钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] cryptograph, PrivateKey key)
			throws Exception {
		/** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
		Cipher cipher = Cipher
				.getInstance(ConfigureEncryptAndDecrypt.RSA_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		/** 执行解密操作 */
		return cipher.doFinal(cryptograph);
	}

	/**
	 * 得到公钥
	 * 
	 * @param key
	 *            密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(String key, String charset)
			throws Exception {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
				Base64.decodeBase64(key.getBytes(charset)));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	/**
	 * 得到私钥
	 * 
	 * @param key
	 *            密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String key, String charset)
			throws Exception {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
				Base64.decodeBase64(key.getBytes(charset)));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	/**
	 * 使用RSA签名
	 * 
	 * @param content
	 * @param privateKey
	 * @return
	 */
	public static String sign(String content, String privateKey) {
		String charset = ConfigureEncryptAndDecrypt.CHAR_ENCODING;
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decodeBase64(privateKey.getBytes(charset)));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			Signature signature = Signature.getInstance("SHA1WithRSA");
			signature.initSign(priKey);
			signature.update(content.getBytes(charset));

			byte[] signed = signature.sign();
			return new String(Base64.encodeBase64(signed));
		} catch (Exception e) {
			System.err.println("RSA Sign Error! content:[" + content
					+ "] privateKey:[" + privateKey + "]" + e);
		}
		return null;
	}

	/**
	 * 验证RSA签名
	 * 
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @return
	 */
	public static boolean checkSign(String content, String sign,
			String publicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decode2(publicKey);
			PublicKey pubKey = keyFactory
					.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature
					.getInstance("SHA1WithRSA");
			signature.initVerify(pubKey);
			signature.update(content
					.getBytes(ConfigureEncryptAndDecrypt.CHAR_ENCODING));

			boolean bverify = signature.verify(Base64.decode2(sign));
			return bverify;
		} catch (Exception e) {
			System.err.println("RSA CheckSign Error! content:[" + content
					+ "] sign:[" + sign + "] publicKey:[" + publicKey + "]" + e);
		}

		return false;
	}

}