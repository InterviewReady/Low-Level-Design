import java.util.*;
import java.util.stream.Collectors;

// least connection, round robin, etc... algorithms can be mentioned by client
// during initialisation
//

abstract class LoadBalancer {
    Map<RequestType, Service> serviceMap;

    public void register(RequestType requestType, Service service) {
        serviceMap.put(requestType, service);
    }

    Set<Destination> getDestinations(Request request){
        Service service = serviceMap.get(request.requestType);
        return service.destinations;
    }

    abstract Destination balanceLoad(Request request);
}

class LeastConnectionLoadBalancer extends LoadBalancer {

    @Override
    Destination balanceLoad(Request request) {
        return getDestinations(request).stream()
                .min(Comparator.comparingInt(d -> d.requestsBeingServed))
                .orElseThrow();
    }
}

class RoutedLoadBalancer extends LoadBalancer {

    @Override
    Destination balanceLoad(Request request) {
        Set<Destination> destinations = getDestinations(request);
        List<Destination> list = destinations.stream().collect(Collectors.toList());
        return list.get(request.id.hashCode() % list.size());
    }
}

class RoundRobinLoadBalancer extends LoadBalancer {
    Map<RequestType, Queue<Destination>> destinationsForRequest;

    @Override
    Destination balanceLoad(Request request) {
        if (!destinationsForRequest.containsKey(request.requestType)){
            Set<Destination> destinations = getDestinations(request);
            destinationsForRequest.put(request.requestType, cTq(destinations));
        }
        Destination destination = destinationsForRequest.get(request.requestType).poll();
        destinationsForRequest.get(request.requestType).add(destination);
        return destination;
    }

    private Queue<Destination> cTq(Set<Destination> destinations) {
        return null;
    }
}

class Service {
    String name;
    Set<Destination> destinations;

    public void addDestination(Destination destination){
        destinations.add(destination);
    }

    public void removeDestination(Destination destination){
        destinations.remove(destination);
    }
}

class Request {
    String id;
    RequestType requestType;
    Map<String, String> parameters;
}

enum RequestType {

}

class Destination {
    String ipAddress;
    int requestsBeingServed;
    int threshold;

    public boolean acceptRequest(Request request) {
        if (threshold <= requestsBeingServed) {
            requestsBeingServed++;
            return true;
        } else {
            return false;
        }
    }

    private void completeRequest(){
        requestsBeingServed--;
    }
}

class LoadBalancerFactory {
    public LoadBalancer createLoadBalancer(String lbType) {
        return switch (lbType) {
            case "round-robin" -> new RoundRobinLoadBalancer();
            case "least-connection" -> new LeastConnectionLoadBalancer();
            default -> new LeastConnectionLoadBalancer();
        };
    }
}
