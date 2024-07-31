package BinaryCalc;

import java.util.*;

public class Q3 {

    public static int precedence(char ch)
    {
        if (ch == '+' || ch == '-')
            return 1;
        if (ch == '*' || ch == '/')
            return 2;
        return 0;
    }



    // Convert infix expression to postfix notation
    public static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<>();
        boolean isDecimal = false; // Flag to indicate if the current number is decimal

        for (int i = 0; i < infix.length(); i++) {
            char ch = infix.charAt(i);
            if (Character.isDigit(ch) || ch == '.') {
                // Handle numbers
                StringBuilder number = new StringBuilder();
                number.append(ch);
                while (i + 1 < infix.length() && (Character.isDigit(infix.charAt(i + 1)) || infix.charAt(i + 1) == '.')) {
                    number.append(infix.charAt(++i));
                }
                postfix.append(number).append(" ");
                isDecimal = number.indexOf(".") != -1; // Check if the number contains a decimal point
            } else if (ch == '(') {
                stack.push(ch);
            } else if (ch == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop()).append(" ");
                }
                stack.pop(); // Discard the '('
            } else if (precedence(ch) > 0) { // Operator
                while (!stack.isEmpty() && precedence(ch) <= precedence(stack.peek())) {
                    postfix.append(stack.pop()).append(" ");
                }
                stack.push(ch);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop()).append(" ");
        }

        return postfix.toString().trim();
    }
    public static double calc(String expression) {
        String postfix = infixToPostfix(expression);
        String[] tokens = postfix.split(" ");
        Stack<Expression> expressions = new Stack<Expression>();
        for (String token : tokens) {
            if (precedence(token.charAt(0))>0) {
                Expression right = expressions.pop();
                Expression left = expressions.pop();
                switch (token) {
                    case "+":
                        expressions.push(new Plus(left, right));
                        break;
                    case "-":
                        expressions.push(new Minus(left, right));
                        break;
                    case "*":
                        expressions.push(new Mul(left, right));
                        break;
                    case "/":
                        expressions.push(new Div(left, right));
                        break;
                }
            }
            else
            {
                expressions.push(new Number(Double.parseDouble(token)));
            }
        }
        Expression expression1 = expressions.pop();
        return expression1.calculate();
    }
}