package org.example;


import java.sql.*;
import java.util.*;

public class App
{
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "3221";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Calculations";
    public static void main( String[] args ) throws Exception {
//test
        Scanner sc = new Scanner(System.in);
        Connection connection = DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);
        while (true){
            System.out.println("1. Calculate an expression");
            System.out.println("2. Show database");
            System.out.println("3. Edit an expression");
            System.out.println("4. Search for an expression");
            System.out.println("5. Close");
            int command = sc.nextInt();
            if  (command == 1){
               Scanner scExp = new Scanner(System.in);
               System.out.println("Enter Expression:");
               String exp = scExp.nextLine();
               String sql = "insert into expressions (expbody, expres) values (?,?)";
               try {
                   PreparedStatement preparedStatement = connection.prepareStatement(sql);
                   preparedStatement.setString(1, exp);
                   preparedStatement.setFloat(2, getResult(exp));
                   preparedStatement.executeUpdate();
               }catch (Exception e){
                   System.out.println("Expression you entered is not valid! Try again");
               }
            }else if (command == 2){
                Statement statement = connection.createStatement();
                String SQL_SELECT_DATA = "select * from expressions";
                ResultSet result = statement.executeQuery(SQL_SELECT_DATA);
                while (result.next()){
                    System.out.println("ID = " + result.getInt("id") + " " + "Expression = "  + result.getString("expbody") + " " + "Result = " + result.getString("expres"));
                }
            }else if (command == 3){
                System.out.println("Enter ID of expression you want to edit");
                int id = sc.nextInt();
                System.out.println("Enter new expression value ");
                Scanner scExp = new Scanner(System.in);
                String exp = scExp.nextLine();
                String sqlUpdate = "update expressions set expbody = ?, expres = ? where id = " + id;
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
                    preparedStatement.setString(1, exp);
                    preparedStatement.setFloat(2, getResult(exp));
                    preparedStatement.executeUpdate();
                }
                catch (Exception e){
                    System.out.println("Expression you entered is not valid! Try again");
                }

            }else if (command == 4){
                System.out.println("Choose a search criteria:");
                System.out.println("1. By ID");
                System.out.println("2. By expression");
                System.out.println("3. By result");
                System.out.println("4. Exit");
                int criteria = sc.nextInt();
                while (criteria!=4) {
                    if (criteria == 1){
                        System.out.println("Enter ID of expression you want to search");
                        int id = sc.nextInt();
                        Statement statement = connection.createStatement();
                        String SQL_SELECT_DATA = "select * from expressions where id =" + id;
                        ResultSet result = statement.executeQuery(SQL_SELECT_DATA);
                        while (result.next()){
                            System.out.println("ID = " + result.getInt("id") + " " + "Expression = "  + result.getString("expbody") + " " + "Result = " + result.getString("expres"));
                        }
                        break;
                } else if (criteria == 2) {
                        System.out.println("Enter expression value you want to search");
                        Scanner scExp = new Scanner(System.in);
                        String exp = scExp.nextLine();
                        Statement statement = connection.createStatement();
                        String SQL_SELECT_DATA = "select * from expressions where expbody Like " + "'" + exp + "'";
                        ResultSet result = statement.executeQuery(SQL_SELECT_DATA);
                        if(result.isBeforeFirst()) {
                            while (result.next()) {
                                System.out.println("ID = " + result.getInt("id") + " " + "Expression = " + result.getString("expbody") + " " + "Result = " + result.getString("expres"));
                            }
                        }else {
                            System.out.println("No results found");
                        }break;
                    }else if (criteria == 3) {
                        System.out.println("Enter a value you want to use for searching");
                        Scanner scRes = new Scanner(System.in);
                        String res = scRes.nextLine();
                        Statement statement = connection.createStatement();
                        System.out.println("Enter filter criteria to use ('>', '<' or '=')");
                        Scanner scFilter = new Scanner(System.in);
                        String filter = scFilter.nextLine();
                        String SQL_SELECT_DATA = "select * from expressions where expres " + filter + "'" + res + "'";
                        ResultSet result = statement.executeQuery(SQL_SELECT_DATA);
                        if(result.isBeforeFirst()) {
                            while (result.next()) {
                                System.out.println("ID = " + result.getInt("id") + " " + "Expression = " + result.getString("expbody") + " " + "Result = " + result.getString("expres"));
                            }
                        }else {
                            System.out.println("No results found");
                        }break;
                    }
                }
            } else if (command == 5) {
                System.exit(0);

            }
        }

    }


    public static Float getResult (String exp) throws Exception {
        Stack<Character> symbols = new Stack<>();
        Stack<Float> numbers = new Stack<>();

        StringBuilder num = new StringBuilder();
        boolean flag = false;
        for (int i = 0; i < exp.length(); i++){
            char el = exp.charAt(i);
            if (Character.isDigit(exp.charAt(i)) || exp.charAt(i) == '.' || (exp.charAt(i) == '-' && Character.isDigit(exp.charAt(i+1)) && !Character.isDigit(exp.charAt(i-1)))){
                while(Character.isDigit(exp.charAt(i)) || exp.charAt(i) == '.' || (exp.charAt(i) == '-'  && Character.isDigit(exp.charAt(i+1)) && !Character.isDigit(exp.charAt(i-1)))){
                    num.append(exp.charAt(i));
                    i++;
                    if(i < exp.length()) {
                        el = exp.charAt(i);
                    }
                    else
                    {
                        break;
                    }

                }
                i--;
                numbers.push(Float.parseFloat(num.toString()));
                flag = false;
                num.setLength(0);
            }
            else if(el=='(')
            {
                if(!exp.contains(")")){
                    throw new Exception("Incorrect brackets balance");
                }
                symbols.push(el);
            }
            else if(el==')')
            {
                if(!symbols.contains('(')){
                    throw new Exception("Incorrect brackets balance");

                }else {
                    while (symbols.peek() != '(') {
                        float output = calculate(numbers, symbols);
                        numbers.push(output);
                    }
                    symbols.pop();
                }

            }
            else if(el == '+' || el == '-' || el == '/' || el == '*' )
            {
                while(!symbols.isEmpty() && priority(el)<=priority(symbols.peek()))
                {
                    float output = calculate(numbers, symbols);
                    numbers.push(output);
                }
                if(!flag) {
                    symbols.push(el);
                    flag = true;
                }else{
                    throw new Exception("Wrong math symbols order");
                }
            }
        }
        while(!symbols.isEmpty())
        {
            float output = calculate(numbers, symbols);
            numbers.push(output);
        }
        float res = numbers.peek();
        System.out.println("Result = " + res);
        return res;
    }
    public static int priority (char c){
        switch (c)
        {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
        }
        return -1;

    }
    public static float calculate(Stack<Float> numbers, Stack<Character> symbols)
    {
        float a = numbers.pop();
        float b = numbers.pop();
        char operation = symbols.pop();
        switch (operation)
        {
            case '+':
                return a + b;
            case '-':
                return b - a;
            case '*':
                return a * b;
            case '/':
                if (a == 0)
                {
                    System.out.println("Cannot divide by zero");
                    return 0;
                }
                return b / a;
        }
        return 0;
    }

}
