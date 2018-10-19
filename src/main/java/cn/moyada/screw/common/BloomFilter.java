package cn.moyada.screw.common;

import cn.moyada.screw.utils.NumberUtil;

public class BloomFilter<T> {

    private static final int DEFAULT_SIZE = Integer.MAX_VALUE >> 1;

    private final HashStrategy[] strategies;

    private final int size;

    private final boolean[] market;

    public BloomFilter() {
        this(DEFAULT_SIZE);
    }

    public BloomFilter(int size) {
        this(size, HashStrategy.getHashStrategy(), HashStrategy.getHigherXorHashStrategy(), HashStrategy.getLowerXorHashStrategy());
    }

    public BloomFilter(HashStrategy... strategies) {
        this(DEFAULT_SIZE, strategies);
    }

    public BloomFilter(int size, HashStrategy... strategies) {
        if (size < 2047) {
            throw new IllegalStateException("size must be greater than 2047.");
        }
        if (strategies.length < 2) {
            throw new IllegalArgumentException("strategy number must be greater than 2.");
        }

        size = NumberUtil.getMax2PowerMinus1(size);

        boolean[] initMarket = new boolean[size];
        for (int i = 0; i < size; i++) {
            initMarket[i] = false;
        }

        this.size = size;
        this.market = initMarket;
        this.strategies = strategies;
    }

    private int getIndex(Object item, HashStrategy strategy) {
        return strategy.getHash(item) & size;
    }

    public void add(T item) {
        for (HashStrategy strategy : strategies) {
            market[getIndex(item, strategy)] = true;
        }
    }

    public boolean exist(T item) {
        int index;
        for (HashStrategy strategy : strategies) {
            index = getIndex(item, strategy);
            if (!market[index]) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        BloomFilter<String> bloomFilter = new BloomFilter<>();
        bloomFilter.add("abcdefgh");
        bloomFilter.add("1234567");
        bloomFilter.add("ZXCASDQWE");
        bloomFilter.add("发个共同好友");
        bloomFilter.add("0*^nfdk&q3");

        System.out.println(bloomFilter.exist("ZXCASDQWE"));
        System.out.println(bloomFilter.exist("12345678"));
    }
}
