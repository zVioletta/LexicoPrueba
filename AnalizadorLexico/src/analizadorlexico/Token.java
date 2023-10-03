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
        AND, OR, IF, ELSE, FOR, NULL, PRINT, RETURN, TRUE, VAR, WHILE, FALSE, FUN,
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

                case 12:
                    if (Character.isDigit(c)) {
                        estado = 14;
                        lexema += c;
                    } else {
                        throw new Exception("Número mal formado: " + lexema);
                    }
                    break;

                case 14:
                    if (Character.isDigit(c)) {
                        estado = 14;
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

                case 13:
                    if (c == '+' || c == '-') {
                        estado = 15;
                        lexema += c;
                    } else if (Character.isDigit(c)) {
                        estado = 16;
                        lexema += c;
                    } else {
                        throw new Exception("Exponente mal formado: " + lexema);
                    }
                    break;

                case 15:
                    if (Character.isDigit(c)) {
                        estado = 16;
                        lexema += c;
                    } else {
                        throw new Exception("Exponente mal formado: " + lexema);
                    }
                    break;

                case 16:
                    if (Character.isDigit(c)) {
                        estado = 16;
                        lexema += c;
                    } else {
                        tokens.add(new TokenInfo(TipoToken.NUMERO, lexema, parseLiteral(lexema)));
                        lexema = "";
                        estado = 0;
                        i--;
                    }
                    break;

                case 17: // PUNTO
                    tokens.add(new TokenInfo(TipoToken.PUNTO, lexema));
                    lexema = "";
                    estado = 0;
                    i--; // Retroceder un caracter para continuar escaneando
                    break;

                case 18: // COMA
                    tokens.add(new TokenInfo(TipoToken.COMA, lexema));
                    lexema = "";
                    estado = 0;
                    i--; // Retroceder un caracter para continuar escaneando
                    break;

                case 19: // MAS
                    tokens.add(new TokenInfo(TipoToken.MAS, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 20: // MENOS
                    tokens.add(new TokenInfo(TipoToken.MENOS, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 21: // MULTIPLICACION
                    tokens.add(new TokenInfo(TipoToken.MULTIPLICACION, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 22: // DIVISION
                    tokens.add(new TokenInfo(TipoToken.DIVISION, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 23: // PUNTO_COMA
                    tokens.add(new TokenInfo(TipoToken.PUNTO_COMA, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 24: // BANG
                    tokens.add(new TokenInfo(TipoToken.BANG, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 25: // DESIGUAL
                    tokens.add(new TokenInfo(TipoToken.DESIGUAL, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 26: // IGUAL
                    tokens.add(new TokenInfo(TipoToken.IGUAL, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 27: // DOBLE_IGUAL
                    tokens.add(new TokenInfo(TipoToken.DOBLE_IGUAL, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 28: // MAYOR_QUE
                    tokens.add(new TokenInfo(TipoToken.MAYOR_QUE, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 29: // MAYOR_IGUAL_QUE
                    tokens.add(new TokenInfo(TipoToken.MAYOR_IGUAL_QUE, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 30: // MENOR_QUE
                    tokens.add(new TokenInfo(TipoToken.MENOR_QUE, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 31: // MENOR_IGUAL_QUE
                    tokens.add(new TokenInfo(TipoToken.MENOR_IGUAL_QUE, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 32: // AND
                    tokens.add(new TokenInfo(TipoToken.AND, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 33: // OR
                    tokens.add(new TokenInfo(TipoToken.OR, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 34: // IF
                    tokens.add(new TokenInfo(TipoToken.IF, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 35: // ELSE
                    tokens.add(new TokenInfo(TipoToken.ELSE, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 36: // FOR
                    tokens.add(new TokenInfo(TipoToken.FOR, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 37: // NULL
                    tokens.add(new TokenInfo(TipoToken.NULL, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 38: // PRINT
                    tokens.add(new TokenInfo(TipoToken.PRINT, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 39: // RETURN
                    tokens.add(new TokenInfo(TipoToken.RETURN, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 40: // TRUE
                    tokens.add(new TokenInfo(TipoToken.TRUE, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 41: // VAR
                    tokens.add(new TokenInfo(TipoToken.VAR, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 42: // WHILE
                    tokens.add(new TokenInfo(TipoToken.WHILE, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 43: // FALSE
                    tokens.add(new TokenInfo(TipoToken.FALSE, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 44: // FUN
                    tokens.add(new TokenInfo(TipoToken.FUN, lexema));
                    lexema = "";
                    estado = 0;
                    i--;
                    break;

                case 45: // STRING
                    if (c == '"') {
                        estado = 0;
                        lexema += c;
                        tokens.add(new TokenInfo(TipoToken.STRING, lexema));
                        lexema = "";
                    } else if (c == '\\') {
                        estado = 46;
                        lexema += c;
                    } else {
                        lexema += c;
                    }
                    break;

                case 46: // Carácter de escape (STRING)
                    lexema += c;
                    estado = 45;
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