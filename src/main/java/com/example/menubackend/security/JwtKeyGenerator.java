package com.example.menubackend.security; // Pode ser em qualquer pacote, por exemplo, o pacote principal

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        // Gera uma chave criptograficamente segura de 512 bits (64 bytes) para HS512
        // e então a codifica em Base64 para ser usada na configuração
        String secretKey = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("SUA NOVA CHAVE SECRETA JWT PARA application.properties:");
        System.out.println(secretKey);
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("COPIE A CHAVE ACIMA E COLE NO SEU application.properties COMO:");
        System.out.println("app.jwt.secret=" + secretKey);
    }
}