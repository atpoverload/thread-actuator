package jdvfs.sandbox;

import static java.util.stream.Collectors.toList;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import jdvfs.Dvfs;

public final class DvfsServer {
  static final int PORT = 50051;

  private static final GetDvfsCpusResponse.CpuInfo buildCpuInfo(int cpu) {
    return GetDvfsCpusResponse.CpuInfo.newBuilder()
        .setCpuId(cpu)
        .addAllGovernor(Arrays.asList(Dvfs.getAvailableGovernors(cpu)))
        .addAllFrequency(Arrays.stream(Dvfs.getAvailableFrequencies(cpu)).boxed().collect(toList()))
        .build();
  }

  private static final List<GetDvfsCpusResponse.CpuInfo> getCpuInfo() {
    return IntStream.range(0, Runtime.getRuntime().availableProcessors())
        .mapToObj(DvfsServer::buildCpuInfo)
        .collect(toList());
  }

  public static void main(String[] args) throws Exception {
    System.out.println("Starting up server");
    var server = ServerBuilder.forPort(PORT).addService(new DvfsService()).build();
    server.start();
    server.awaitTermination();
  }

  private static class DvfsService extends DvfsServiceGrpc.DvfsServiceImplBase {
    @Override
    public void getDvfsCpus(
        GetDvfsCpusRequest request, StreamObserver<GetDvfsCpusResponse> responseObserver) {
          System.out.println("????");
      responseObserver.onNext(GetDvfsCpusResponse.newBuilder().addAllCpu(getCpuInfo()).build());
      responseObserver.onCompleted();
    }
  }
}
