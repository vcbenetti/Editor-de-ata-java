import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberToWordsConverter {

    // Arrays for Brazilian Portuguese number words
    private static final String[] PT_UNITS = {
            "", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez",
            "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete",
            "dezoito", "dezenove"
    };

    private static final String[] PT_TENS = {
            "", "", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa"
    };

    private static final String[] PT_HUNDREDS = {
            "", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos", "seiscentos",
            "setecentos", "oitocentos", "novecentos"
    };

    // Scales for thousands, millions, billions
    // Index 0: no suffix (units group)
    // Index 1: "mil" (singular and plural for thousands)
    // Index 2: "milhão" (singular), "milhões" (plural)
    // Index 3: "bilhão" (singular), "bilhões" (plural)
    private static final String[][] PT_SCALES = {
            {"", ""},
            {"mil", "mil"},
            {"milhão", "milhões"},
            {"bilhão", "bilhões"}
    };

    /**
     * Converts a number less than one thousand (0-999) to Brazilian Portuguese words.
     * Handles the "cem" vs "cento" distinction and the "e" conjunction for tens/units.
     *
     * @param n The number (0-999) to convert.
     * @return The number in Brazilian Portuguese words.
     */
    private static String convertLessThanOneThousandPt(long n) {
        if (n == 0) return ""; // For internal use, 0 is typically handled at the top level

        String s = "";
        int hundredsDigit = (int) (n / 100);
        int remainder = (int) (n % 100);

        if (hundredsDigit > 0) {
            if (hundredsDigit == 1 && remainder == 0) {
                s += "cem"; // Special case for exactly 100
            } else {
                s += PT_HUNDREDS[hundredsDigit]; // "cento", "duzentos", etc.
            }
        }

        if (remainder > 0) {
            if (s.length() > 0) {
                s += " e "; // Add "e" if there are hundreds (e.g., "cento e vinte")
            }
            if (remainder < 20) {
                s += PT_UNITS[remainder]; // "um", "doze", "dezenove"
            } else {
                int tensDigit = remainder / 10;
                int unitsDigit = remainder % 10;
                s += PT_TENS[tensDigit]; // "vinte", "trinta"
                if (unitsDigit > 0) {
                    s += " e " + PT_UNITS[unitsDigit]; // Add "e" for tens and units (e.g., "vinte e um")
                }
            }
        }
        return s;
    }

    /**
     * Converts a long number to its Brazilian Portuguese word representation.
     * Handles large numbers (thousands, millions, billions) and the specific "e" conjunction rules.
     *
     * @param n The long number to convert.
     * @return The number in Brazilian Portuguese words.
     */
    public static String convert(long n) {
        if (n == 0) {
            return "zero";
        }
        if (n < 0) {
            return "menos " + convert(-n);
        }

        int scaleCounter = 0;
        List<String> parts = new ArrayList<>();
        long numToProcess = n;

        while (numToProcess > 0) {
            long chunk = numToProcess % 1000;
            numToProcess /= 1000;

            String chunkWords = convertLessThanOneThousandPt(chunk);
            String scaleWord = "";

            if (scaleCounter > 0) { // Not the units chunk
                if (chunk == 1) { // Special handling for "mil", "um milhão"
                    if (scaleCounter == 1) { // "mil" (thousand)
                        scaleWord = PT_SCALES[scaleCounter][0];
                    } else { // "um milhão", "um bilhão"
                        scaleWord = "um " + PT_SCALES[scaleCounter][0];
                    }
                } else if (chunk > 1) {
                    scaleWord = PT_SCALES[scaleCounter][1]; // Plural "milhões", "bilhões"
                }
            }

            if (!chunkWords.isEmpty() || !scaleWord.isEmpty()) {
                String combined = chunkWords;
                if (!scaleWord.isEmpty()) {
                    if (scaleCounter == 1 && chunk == 1) { // "mil"
                        combined = scaleWord;
                    } else if (scaleCounter > 1 && chunk == 1) { // "um milhão"
                        combined = scaleWord; // scaleWord already contains "um "
                    } else {
                        combined += " " + scaleWord;
                    }
                }
                parts.add(combined);
            }
            scaleCounter++;
        }

        Collections.reverse(parts); // Order from largest magnitude to smallest

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            String currentPart = parts.get(i);

            if (result.length() > 0) {
                // Determine connector: "e" vs ", "
                boolean isLastPart = (i == parts.size() - 1);
                // Original numeric value of the last chunk of the *original* number
                long lastNumericChunkValue = (n % 1000);

                // Add "e" if it's the last part AND its value is between 1 and 99 (inclusive)
                // AND there were preceding parts (i.e., not a single-chunk number)
                if (isLastPart && lastNumericChunkValue > 0 && lastNumericChunkValue < 100) {
                    result.append(" e ");
                }
                // Special case for 'cem' following a thousands/millions group (e.g. 2.000.100 -> "dois milhões e cem")
                // Check if this part is 'cem' AND it's not the only part, AND the last numeric chunk (n%1000) is exactly 100
                else if (isLastPart && lastNumericChunkValue == 100 && parts.size() > 1) {
                    result.append(" e ");
                }
                else {
                    result.append(", "); // Default separator for intermediate major groups
                }
            }
            result.append(currentPart);
        }

        return result.toString().trim().replaceAll(" +", " ");
    }

    /**
     * Converts a double (typically a currency value) to Brazilian Portuguese words,
     * including "reais" and "centavos".
     *
     * @param d The double value to convert (e.g., 123.45).
     * @return The currency value in Brazilian Portuguese words.
     */
    public static String convertDecimal(double d) {
        if (d == 0) {
            return "zero reais"; // Return "zero reais" for currency context
        }

        BigDecimal bd = new BigDecimal(String.valueOf(d));
        bd = bd.setScale(2, RoundingMode.HALF_UP); // Ensure 2 decimal places

        long wholePart = bd.longValue();
        BigDecimal fractionalBd = bd.remainder(BigDecimal.ONE).abs();
        long cents = fractionalBd.multiply(new BigDecimal(100)).longValue();

        String wholeWords = convert(wholePart);
        // Correctly handle "um real" vs "reais"
        String currencyUnit = (wholePart == 1) ? "real" : "reais";

        String fractionalWords = "";
        if (cents > 0) {
            // Moved declarations here to ensure they are in scope
            String centsWords = convert(cents);
            String centsUnit = (cents == 1) ? "centavo" : "centavos";
            fractionalWords = " e " + centsWords + " " + centsUnit;
        }

        // Combine whole part with "reais" and fractional part with "centavos"
        // And use "e" if both whole and fractional parts exist, or just "e" for "zero reais e ... centavos"
        if (wholePart == 0 && cents > 0) {
            // Now centsWords and centsUnit are correctly scoped
            String centsWords = convert(cents); // Re-calculate as it's used directly here
            String centsUnit = (cents == 1) ? "centavo" : "centavos";
            return (centsWords + " " + centsUnit).trim(); // Only centavos, e.g., "vinte e cinco centavos"
        } else if (wholePart > 0 && cents == 0) {
            return (wholeWords + " " + currencyUnit).trim(); // Only reais, e.g., "cem reais"
        } else { // Both whole and cents exist
            return (wholeWords + " " + currencyUnit + fractionalWords).trim();
        }
    }
}
