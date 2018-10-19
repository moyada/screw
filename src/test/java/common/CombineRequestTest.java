package common;

import cn.moyada.screw.cache.CombineRequest;
import cn.moyada.screw.cache.ZookeeperCombineRequest;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author xueyikang
 * @create 2018-03-14 15:46
 */
public class CombineRequestTest {

    public static void main(String[] args) throws InterruptedException {
//        CombineRequest combineRequest = new CASCombineRequest();
        CombineRequest combineRequest = new ZookeeperCombineRequest("127.0.0.1:2181", "invoke");

        CombineRequestDemo c1 = new CombineRequestDemo(combineRequest);

        CompletableFuture.runAsync(() -> System.out.println(c1.get("test1")));

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            CompletableFuture.runAsync(() -> System.out.println(finalI + c1.get("test" + (finalI & 15))));
        }

        CompletableFuture.runAsync(() -> System.out.println(c1.get("test")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test1")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test1")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test1")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test3")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test1")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test2")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test2")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test2")));
        CompletableFuture.runAsync(() -> System.out.println(c1.get("test")));
        System.out.println("done");
        Thread.sleep(30000);
        c1.close();
    }
}

class CombineRequestDemo {

    private CombineRequest combineRequest;


    private Map<String, String> cacheMap;

    private ExecutorService pool;

    public CombineRequestDemo(CombineRequest combineRequest) {
        this.combineRequest = combineRequest;
        this.cacheMap = new ConcurrentHashMap<>();
        this.pool = Executors.newFixedThreadPool(100);
    }

    public void close() {
        pool.shutdownNow();
    }

    public String get(String key) {
        Future<String> result = pool.submit(() -> {
            String r = cacheMap.get(key);
            if(null != r) {
                return r;
            }
            combineRequest.waitIfRunning(key);

            r = cacheMap.get(key);
            if(null != r) {
                return r;
            }
            System.out.println("find " + key);
            r = key + " ok";
            cacheMap.put(key, r);
            combineRequest.notifyWait(key);
            return r;
        });
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}