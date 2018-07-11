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

    private long convert(double size, CapacityUnit unit) {
        return (long) (unit.size * size);
    }

    private long convert(long size, CapacityUnit unit) {
        return unit.size * size;
    }

    protected double calculate(long size) {
        return size * 1D / this.size;
    }

    public static void main(String[] args) {
        System.out.println(CapacityUnit.GB.calculate(1030*240*24, CapacityUnit.KB));
    }
}
