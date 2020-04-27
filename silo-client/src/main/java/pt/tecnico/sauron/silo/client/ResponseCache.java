package pt.tecnico.sauron.silo.client;

import pt.tecnico.sauron.silo.grpc.ClientResponse;

import java.util.*;

public class ResponseCache {

    private int limit = 20;

    private LinkedHashMap<List<String>,ClientResponse> cacheMap = new LinkedHashMap<>(){
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

    public void addEntry(List<String> list, ClientResponse response){
        this.cacheMap.put(list,response);
        System.out.println("TAMANHO-->" + this.cacheMap.size());
        System.out.println(this.cacheMap);
    }

    public ClientResponse getLastRead(List<String> list, ClientResponse response){
        for(List<String> key : this.cacheMap.keySet()){
            if(list.equals(key))
                return this.cacheMap.get(key);
        }
        return response;
    }
}
