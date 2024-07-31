package Config;


import graph.BinOpAgent;

public class testConfig implements Config {

    @Override
    public void create() {
        new BinOpAgent("plus", "A", "B", "R1", (x, y)->x+y);
        new BinOpAgent("minus", "A", "B", "R2", (x,y)->x-y);
        new BinOpAgent("pow", "R1", "R2", "R3", (x,y)->Math.pow(x,y));
        new BinOpAgent("div", "R3", "R2", "R4", (x,y)->x/y);
    }

    @Override
    public String getName() {
        return "Math Example";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {

    }

}
