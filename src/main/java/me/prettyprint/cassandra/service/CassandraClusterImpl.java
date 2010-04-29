package me.prettyprint.cassandra.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TokenRange;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

/**
 * Implementation of the {@link CassandraCluster} interface.
 * 
 * @author Nate McCall (nate@vervewireless.com)
 */
/*package*/ class CassandraClusterImpl implements CassandraCluster {

  private CassandraClient cassandraClient;
  
  public CassandraClusterImpl(CassandraClient cassandraClient) {
    this.cassandraClient = cassandraClient;
  }
  
  @Override
  public Set<String> describeKeyspaces() throws TTransportException, TException {
    return cassandraClient.getCassandra().describe_keyspaces();
  }

  @Override
  public String describeClusterName() throws TTransportException, TException {
    return cassandraClient.getCassandra().describe_cluster_name();
  }

  @Override
  public String describeThriftVersion() throws TTransportException, TException {
    return cassandraClient.getCassandra().describe_version();
  }

  @Override
  public List<TokenRange> describeRing(Keyspace keyspace) throws TTransportException, TException {
    return cassandraClient.getCassandra().describe_ring(keyspace.getName());
  }

  @Override
  public Set<String> getHostNames() throws TTransportException, TException, NotFoundException {
    Set<String> hostnames = new HashSet<String>();
    for (String keyspace : describeKeyspaces()) {
      if (!keyspace.equals("system")) {
        List<TokenRange> tokenRanges = describeRing(cassandraClient.getKeyspace(keyspace));
        for (TokenRange tokenRange : tokenRanges) {
          for (String host : tokenRange.getEndpoints()){
            hostnames.add(host);
          }
        }
        break;
      }
    }
    return hostnames;
  }

  @Override
  public Map<String, Map<String, String>> describeKeyspace(Keyspace keyspace)
      throws TTransportException, TException, NotFoundException {
  
    return cassandraClient.getCassandra().describe_keyspace(keyspace.getName());
  }

  
}