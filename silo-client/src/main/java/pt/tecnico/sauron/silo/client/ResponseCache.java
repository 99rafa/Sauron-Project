package pt.tecnico.sauron.silo.client;

import pt.tecnico.sauron.silo.grpc.ClientResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResponseCache{

    private int limit = 20;

    private LinkedHashMap<List<String>, ClientResponse> cacheMap = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry eldest) {
            return size() > limit;
        }
    };

    public ResponseCache() {
    }

    public ResponseCache(int limit) {
        this.limit = limit;
    }

    public void addEntry(List<String> list, ClientResponse response) {
        this.cacheMap.put(list, response);
    }


    // get last read response because request was not updated
    public ClientResponse getLastRead(List<String> list, ClientResponse response) {

        for (List<String> key : this.cacheMap.keySet()) {
            if (list.equals(key)) {
                System.out.println("Response outdated.\nRetrieving last stable entry from cache...");
                return this.cacheMap.get(key);
            }
        }
        return response;
    }
}
