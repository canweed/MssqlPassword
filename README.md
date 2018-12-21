# MssqlPassword

- Mssql PWDENCRYPT PWDCOMPARE java version  
- No dependencies  
- <span style="color:  #24292e;;">free licenses</span>

## Usage

``` java
// encoding
String endPw = MssqlPassword.pwdEncrypt("abcd");

// compare
boolean succ = MssqlPassword.pwdCompare("abcd", "0x0200638B956A888EB630D0BC549CEDAE920663F02B2E8DF7F10C1D5A5847651188367D77D0F72BC65FFBD4DF24D51EE57F77C5EFC1C69790F92D65CC599F6486A39521C5B7AA");
```
