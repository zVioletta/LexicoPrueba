package analizadorlexico;

import analizadorlexico.Token.TipoToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ALexic {

    static boolean existenErrores = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Tienes que introducir por lo menos un token.");

            // Convención definida en el archivo "system.h" de UNIX
            System.exit(64);
        } else if (args.length == 1) {
            ejecutarArchivo(args[0]);
        } else {
            ejecutarPrompt();
        }
    }

    private static void ejecutarArchivo(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        ejecutar(new String(bytes, Charset.defaultCharset()));

        // Se indica que existe un error
        if (existenErrores) System.exit(65);
    }

    private static void ejecutarPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (; ; ) {
            System.out.print(">>> ");
            String linea = reader.readLine();
            if (linea == null) break; // Presionar Ctrl + D
            ejecutar(linea);
            existenErrores = false;
        }
    }

    private static void ejecutar(String source) {
        try {
            Token tokenGenerator = new Token();
            List<Token.TokenInfo> tokens = tokenGenerator.escanear(source);

            for (Token.TokenInfo tokenInfo : tokens) {
                TipoToken tipo = tokenInfo.getTipo();
                String lexema = tokenInfo.getLexema();
                Object valorLiteral = tokenInfo.getLiteral();

                System.out.print("Tipo de Token: " + tipo);

                if (!lexema.isEmpty()) {
                    System.out.print(", Lexema: " + lexema);
                }

                if (valorLiteral != null) {
                    System.out.print(", Valor Literal: " + valorLiteral);
                }

                System.out.println();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void error(int linea, String mensaje) {
        reportar(linea, "", mensaje);
    }

    private static void reportar(int linea, String posicion, String mensaje) {
        System.err.println(
                "[linea " + linea + "] Error " + posicion + ": " + mensaje
        );
        existenErrores = true;
    }
}