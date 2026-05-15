import util.InputValidator;

public class VerifyId {
    public static void main(String[] args) {
        String id = "202514607";
        boolean valid = InputValidator.isValidStudentIdAlgorithm(id);
        String error = InputValidator.validateStudentId(id);
        
        System.out.println("ID: " + id);
        System.out.println("Algorithm valid: " + valid);
        System.out.println("Validation error: " + error);
        
        int sum = 0;
        for (char c : id.toCharArray()) {
            sum += Character.getNumericValue(c);
        }
        System.out.println("Sum of digits: " + sum);
        System.out.println("Sum % 10: " + (sum % 10));
    }
}
