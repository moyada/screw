package cn.moyada.screw.enums;

/**
 * @author xueyikang
 * @create 2018-07-12 03:00
 */
public enum CapacityUnit {

    B(1),
    KB(2),
    MB(3),
    GB(4),
    TB(5);

    private long size;

    CapacityUnit(int level) {
        this.size = 1;
        for (int i = 0; i < level - 1; i++) {
            size *= 1024;
        }
    }

    public double calculate(long size, CapacityUnit unit) {
        if(size < 0) {
            throw new IllegalArgumentException();
        }
        return calculate(convert(size, unit));
    }

    public double calculate(double size, CapacityUnit unit) {
        if(size < 0D) {
            throw new IllegalArgumentException();
        }
        return calculate(convert(size, unit));
    }

    private double convert(double size, CapacityUnit unit) {
        return unit.size * size;
    }

    private double convert(long size, CapacityUnit unit) {
        return unit.size * size;
    }

    protected double calculate(double size) {
        return size / this.size;
    }

    public static void main(String[] args) {
        System.out.println(CapacityUnit.MB.calculate(3.879D, CapacityUnit.GB));
    }
}
