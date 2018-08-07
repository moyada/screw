package cn.moyada.screw.common;

import java.io.Serializable;

public interface HashStrategy extends Serializable {

    int getHash(Object object);

    static HashStrategy getHashStrategy() {
        return new HashStrategy() {
            private static final long serialVersionUID = 7695264591962460592L;

            @Override
            public int getHash(Object object) {
                return object.hashCode();
            }
        };
    }

    static HashStrategy getHigherXorHashStrategy() {
        return new HashStrategy() {
            private static final long serialVersionUID = -8988016946682795202L;

            @Override
            public int getHash(Object object) {
                int hashCode = object.hashCode();
                return hashCode ^ (hashCode >> 16);
            }
        };
    }

    static HashStrategy getLowerXorHashStrategy() {
        return new HashStrategy() {
            private static final long serialVersionUID = -8477082806010304626L;

            @Override
            public int getHash(Object object) {
                int hashCode = object.hashCode();
                return hashCode ^ (hashCode << 16);
            }
        };
    }
}
