package BinaryCalc;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Div extends BinaryExpression{
    public Div(Expression left, Expression right)
    {
        super(left,right);
    }

    @Override
    public double calculate() {
        BigDecimal leftCalc = BigDecimal.valueOf(super.left.calculate());
        BigDecimal rightCalc = BigDecimal.valueOf(super.right.calculate());
        int scale = 2; // For example, rounding to 2 decimal places
        RoundingMode roundingMode = RoundingMode.HALF_UP; // You can choose any rounding mode that suits your needs

        BigDecimal result = leftCalc.divide(rightCalc, scale, roundingMode);
        return result.doubleValue();
    }
}
