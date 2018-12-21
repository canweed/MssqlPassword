package com.imd.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

/**
 * MssqlPassword
 * 
 * mssql 용 비밀번호를 생성하고 비교한다.
 * 
 * 생성 : MssqlPassword.pwdEncrypt("abcd")
 * 비교 : MssqlPassword.pwdCompare("abcd", "0x0200638B956A888EB630D0BC549CEDAE920663F02B2E8DF7F10C1D5A5847651188367D77D0F72BC65FFBD4DF24D51EE57F77C5EFC1C69790F92D65CC599F6486A39521C5B7AA")
 * 
 * @author accplus ( canweed@imdglobals.com )
 */
public class MssqlPassword {

	/** <pre>
	 * mssql 비밀번호를 생성한다.
	 * MssqlPassword.pwdEncrypt("abcd")
	 * @param password
	 * @return mssql용으로 생성된 비밀번호
	 */
	public static String pwdEncrypt(String password) {
		try {

			if (password == null) password = "";
			// 1. salt 4 byte 만들기. mssql 형식은 4 byte 임

			byte[] salt = new byte[4];
			SecureRandom random = new SecureRandom();
			random.nextBytes(salt);

			// 2. sha512

			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(password.getBytes(StandardCharsets.UTF_16LE)); // mssql 의 nvarchar 는 UTF_16LE 이다.
			md.update(salt);
			byte[] encoded = md.digest();

			// 3. mssql PwdEncrypt 형식에 맞춰서 결과 반환

			return "0x0200" + DatatypeConverter.printHexBinary(salt) + DatatypeConverter.printHexBinary(encoded);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
	}

	/**
	 * mssql 비밀번호로 생성된 문자를 검증한다.
	 * @param password 사용자 입력 비밀번호
	 * @param dbPassword 디비에서 입력된 비밀번호
	 * @return 맞으면 true
	 */
	public static boolean pwdCompare(String password, String dbPassword) {
		try {

			// mssql 형식이 아니면 실패
			if (password == null || !dbPassword.startsWith("0x0200")) return false;

			// 1. salt과 encoded 영역 분리
			byte[] salt = DatatypeConverter.parseHexBinary(dbPassword.substring(6, 14));
			byte[] encodedDb = DatatypeConverter.parseHexBinary(dbPassword.substring(14));

			// 2. 다이제스트 축출
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(password.getBytes(StandardCharsets.UTF_16LE));
			md.update(salt);
			byte[] encoded = md.digest();

			// 3. 비교
			return Arrays.equals(encoded, encodedDb);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
	}

	public static void main(String[] args) {

		final String endPwd = pwdEncrypt("abcd");
		System.out.println("RESULT : " + endPwd);
		System.out.println(pwdCompare("abcd",
				"0x0200638B956A888EB630D0BC549CEDAE920663F02B2E8DF7F10C1D5A5847651188367D77D0F72BC65FFBD4DF24D51EE57F77C5EFC1C69790F92D65CC599F6486A39521C5B7AA"));
	}
}
