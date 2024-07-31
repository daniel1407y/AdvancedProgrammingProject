package BinaryCalc;

import java.math.BigDecimal;

public class Minus extends BinaryExpression{
    public Minus(Expression left, Expression right)
    {
        super(left,right);
    }

    @Override
    public double calculate() {
        BigDecimal leftCalc = BigDecimal.valueOf(super.left.calculate());
        BigDecimal rightCalc = BigDecimal.valueOf(super.right.calculate());
        return leftCalc.subtract(rightCalc).doubleValue();
    }
}
