package pl.ciborowski.konrad.calculator;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import java.util.LinkedList;
import java.util.List;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.joining;

public class StringCalculator {

    static final String NEGATIVE_NUMBER_ERROR_MESSAGE = "Negative numbers not allowed: ";

    public int add(String numbers) {
        List<String> delimiters;
        String coreNumbersString;
        if (numbers.startsWith("//")) {
            int endOfDelimiters = numbers.indexOf("\\n");
            if (endOfDelimiters < 0) {
                throw new IllegalArgumentException("No matching delimiter marker in " + numbers);
            }
            delimiters = extractDelimiters(numbers.substring(0, endOfDelimiters));
            delimiters.add("\n");
            coreNumbersString = numbers.substring(endOfDelimiters + "\\n".length());
        } else {
            delimiters = List.of(",", "\n");
            coreNumbersString = numbers;
        }
        return addNumbers(coreNumbersString, delimiters);
    }

    private List<String> extractDelimiters(String delimiterString) {
        List<String> delimiters = new LinkedList<>();
        int leftBracketIndex = 0;
        while ((leftBracketIndex = delimiterString.indexOf('[', leftBracketIndex)) > 0) {
            int rightBracketIndex = delimiterString.indexOf(']', leftBracketIndex);
            if (rightBracketIndex < 0) {
                throw new IllegalArgumentException("Right bracket missing in " + delimiterString);
            }
            String delimiter = delimiterString.substring(leftBracketIndex + 1, rightBracketIndex);
            if (delimiter.isEmpty()) {
                throw new IllegalArgumentException("Empty delimiter in " + delimiterString);
            }
            if (delimiter.matches(".*\\d.*")) {
                throw new IllegalArgumentException("Digits not allowed in delimiters in " + delimiterString);
            }
            delimiters.add(delimiter);
            leftBracketIndex = rightBracketIndex + 1;
        }
        return delimiters;
    }

    private int addNumbers(String numbers, List<String> delimiters) {
        if (numbers.isEmpty()) {
            return 0;
        }
        StringBuilder splitter = new StringBuilder();
        String tokens[] = splitIntoTokens(delimiters, splitter, numbers);
        List<Integer> negatives = new LinkedList<>();
        int sum = 0;
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            try {
                int number = parseInt(token);
                if (number < 0) {
                    negatives.add(number);
                }
                if (number > 1000) {
                    number = 0;
                }
                sum += number;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(token + " is not a number in " + numbers);
            }
        }
        if (negatives.isEmpty()) {
            return sum;
        } else {
            throw buildExceptionWithNegatives(negatives);
        }
    }

    private IllegalArgumentException buildExceptionWithNegatives(List<Integer> negatives) throws IllegalArgumentException {
        StringBuilder error = new StringBuilder(NEGATIVE_NUMBER_ERROR_MESSAGE);
        error.append(negatives.stream().map(n -> valueOf(n)).collect(joining(",")));
        throw new IllegalArgumentException(error.toString());
    }

    private String[] splitIntoTokens(List<String> delimiters, StringBuilder splitter, String numbers) {
        delimiters.forEach(delimiter -> {
            if (splitter.length() > 0) {
                splitter.append("|");
            }
            splitter.append(quote(delimiter));
        });
        return numbers.split(splitter.toString());
    }

}
