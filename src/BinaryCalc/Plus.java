package BinaryCalc;

import java.math.BigDecimal;

public class Plus extends BinaryExpression{
    public Plus(Expression left, Expression right)
    {
        super(left,right);
    }


    @Override
    public double calculate() {
        BigDecimal leftCalc = BigDecimal.valueOf(super.left.calculate());
        BigDecimal rightCalc = BigDecimal.valueOf(super.right.calculate());
        return leftCalc.add(rightCalc).doubleValue();
    }
}
