package analizadorlexico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Token {
    public enum TipoToken {
        NUMERO,
        IDENTIFICADOR,
        STRING,
        PUNTO, COMA, MAS, MENOS, MULTIPLICACION, DIVISION, PUNTO_COMA,
        BANG, DESIGUAL, IGUAL, DOBLE_IGUAL, MAYOR_QUE, MAYOR_IGUAL_QUE, MENOR_QUE, MENOR_IGUAL_QUE,
        AND, OR, IF, ELSE, FOR, NULL, PRINT, RETURN, TRUE, VAR, WHILE, FALSE, FUN, OPERADOR,
        EOF
    }

    private final List<TokenInfo> tokens = new ArrayList<>();
    private Map<String, TipoToken> palabrasReservadas = new HashMap<>();

    public Token() {
        // Inicializar palabras reservadas
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and", TipoToken.AND);
        palabrasReservadas.put("or", TipoToken.OR);
        palabrasReservadas.put("if", TipoToken.IF);
        palabrasReservadas.put("else", TipoToken.ELSE);
        palabrasReservadas.put("for", TipoToken.FOR);
        palabrasReservadas.put("null", TipoToken.NULL);
        palabrasReservadas.put("print", TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true", TipoToken.TRUE);
        palabrasReservadas.put("var", TipoToken.VAR);
        palabrasReservadas.put("while", TipoToken.WHILE);
        palabrasReservadas.put("false", TipoToken.FALSE);
        palabrasReservadas.put("fun", TipoToken.FUN);
    }

    public List<TokenInfo> escanear(String codigoFuente) throws Exception {
        String lexema = "";
        int estado = 0;

        for (int i = 0; i < codigoFuente.length(); i++) {
            char c = codigoFuente.charAt(i);

            switch (estado) {
                case 0:
                    if (Character.isLetter(c)) {
                        estado = 9;
                        lexema += c;
                    } else if (Character.isDigit(c)) {
                        estado = 11;
                        lexema += c;
                    } else if (c == ' ' || c == '\n' || c == '\t') {
                        // Ignorar espacios en blanco, saltos de línea y tabulaciones
                    } else {
                        lexema += c;
                        if (palabrasReservadas.containsKey(lexema)) {
                            TipoToken tt = palabrasReservadas.get(lexema);
                            tokens.add(new TokenInfo(tt, lexema));
                            lexema = "";
                        } else {
                            for (TipoToken tipo : TipoToken.values()) {
                                if (tipo.toString().equals(lexema)) {
                                    tokens.add(new TokenInfo(tipo, lexema));
                                    lexema = "";
                                    estado = 0;
                                    break;
                                }
                            }
                        }
                    }
                    break;

                case 9:
                    if (Character.isLetter(c) || Character.isDigit(c)) {
                        estado = 9;
                        lexema += c;
                    } else {
                        if (!lexema.isEmpty()) {
                            tokens.add(new TokenInfo(TipoToken.IDENTIFICADOR, lexema));
                            lexema = "";
                        }
                        i--;
                        estado = 0;
                    }
                    break;

                case 11:
                    if (Character.isDigit(c)) {
                        estado = 11;
                        lexema += c;
                    } else if (c == '.') {
                        estado = 12;
                        lexema += c;
                    } else if (c == 'E') {
                        estado = 13;
                        lexema += c;
                    } else {
                        tokens.add(new TokenInfo(TipoToken.NUMERO, lexema, parseLiteral(lexema)));
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;

                case 13: // OPERADOR
                    if (c == '+') {
                        tokens.add(new TokenInfo(TipoToken.MAS, lexema));
                        lexema = "";
                        estado = 0;
                    } else if (c == '-') {
                        tokens.add(new TokenInfo(TipoToken.MENOS, lexema));
                        lexema = "";
                        estado = 0;
                    } else if (c == '*') {
                        tokens.add(new TokenInfo(TipoToken.MULTIPLICACION, lexema));
                        lexema = "";
                        estado = 0;
                    } else if (c == '/') {
                        tokens.add(new TokenInfo(TipoToken.DIVISION, lexema));
                        lexema = "";
                        estado = 0;
                    } else if (c == '&') {
                        if (i == codigoFuente.length() - 1) {
                            throw new Exception("Operador binario no válido: " + lexema);
                        }
                        if (codigoFuente.charAt(i + 1) == '&') {
                            tokens.add(new TokenInfo(TipoToken.AND, lexema));
                            lexema = "";
                            estado = 0;
                            i++;
                        } else {
                            tokens.add(new TokenInfo(TipoToken.OPERADOR, lexema));
                            lexema = "";
                            estado = 0;
                        }
                    } else if (c == '|') {
                        if (i == codigoFuente.length() - 1) {
                            throw new Exception("Operador binario no válido: " + lexema);
                        }
                        if (codigoFuente.charAt(i + 1) == '|') {
                            tokens.add(new TokenInfo(TipoToken.OR, lexema));
                            lexema = "";
                            estado = 0;
                            i++;
                        } else {
                            tokens.add(new TokenInfo(TipoToken.OPERADOR, lexema));
                            lexema = "";
                            estado = 0;
                        }
                    } else if (c == '<') {
                        if (i == codigoFuente.length() - 1) {
                            throw new Exception("Operador binario no válido: " + lexema);
                        }
                        if (codigoFuente.charAt(i + 1) == '=') {
                            tokens.add(new TokenInfo(TipoToken.MENOR_IGUAL_QUE, lexema));
                            lexema = "";
                            estado = 0;
                            i++;
                        } else {
                            tokens.add(new TokenInfo(TipoToken.MENOR_QUE, lexema));
                            lexema = "";
                            estado = 0;
                        }
                    } else if (c == '>') {
                        if (i == codigoFuente.length() - 1) {
                            throw new Exception("Operador binario no válido: " + lexema);
                        }
                        if (codigoFuente.charAt(i + 1) == '=') {
                            tokens.add(new TokenInfo(TipoToken.MAYOR_IGUAL_QUE, lexema));
                            lexema = "";
                            estado = 0;
                            i++;
                        } else {
                            tokens.add(new TokenInfo(TipoToken.MAYOR_QUE, lexema));
                            lexema = "";
                            estado = 0;
                        }
                    }
                    break;

                case 12: // PUNTO
                    if (Character.isDigit(c)) {
                        estado = 14;
                        lexema += c;
                    } else {
                        tokens.add(new TokenInfo(TipoToken.PUNTO, lexema));
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;

                case 14: // NÚMERO DECIMAL
                    if (Character.isDigit(c)) {
                        estado = 14;
                        lexema += c;
                    } else if (c == 'E') {
                        estado = 15;
                        lexema += c;
                    } else {
                        tokens.add(new TokenInfo(TipoToken.NUMERO, lexema, parseLiteral(lexema)));
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;

                case 15: // EXPONENTE
                    if (c == '+' || c == '-') {
                        estado = 16;
                        lexema += c;
                    } else if (Character.isDigit(c)) {
                        estado = 17;
                        lexema += c;
                    } else {
                        throw new Exception("Exponente no válido: " + lexema);
                    }
                    break;

                case 16: // SIGNO DE EXPONENTE
                    if (Character.isDigit(c)) {
                        estado = 17;
                        lexema += c;
                    } else {
                        throw new Exception("Exponente no válido: " + lexema);
                    }
                    break;

                case 17: // DIGITOS DE EXPONENTE
                    if (Character.isDigit(c)) {
                        estado = 17;
                        lexema += c;
                    } else {
                        tokens.add(new TokenInfo(TipoToken.NUMERO, lexema, parseLiteral(lexema)));
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;

                case 45: // COMA
                    if (c == ',') {
                        tokens.add(new TokenInfo(TipoToken.COMA, lexema));
                        lexema = "";
                        estado = 0;
                        i++;
                    } else {
                        throw new Exception("Caracter no válido: " + c);
                    }
                    break;

                case 46: // COMENTARIO
                    if (c == '\n') {
                        estado = 0;
                        lexema = "";
                    } else {
                        lexema += c;
                    }
                    break;

                default:
                    throw new Exception("Estado no válido: " + estado);
            }
        }

        return tokens;
    }

    private Object parseLiteral(String lexema) {
        try {
            if (lexema.contains(".")) {
                return Double.valueOf(lexema);
            } else {
                return Integer.valueOf(lexema);
            }
        } catch (NumberFormatException e) {
            return lexema; // Si no es un número válido, retornar como cadena
        }
    }

    public class TokenInfo {
        final TipoToken tipo;
        final String lexema;
        final Object literal;

        public TokenInfo(TipoToken tipo, String lexema) {
            this.tipo = tipo;
            this.lexema = lexema;
            this.literal = null; // Inicializar literal con null
        }

        public TokenInfo(TipoToken tipo, String lexema, Object literal) {
            this.tipo = tipo;
            this.lexema = lexema;
            this.literal = literal;
        }

        public TipoToken getTipo() {
            return tipo;
        }

        public String getLexema() {
            return lexema;
        }

        public Object getLiteral() {
            return literal;
        }

        public String toString() {
            return "<" + tipo + ", " + lexema + ">";
        }
    }
}