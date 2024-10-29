import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.exception.NotFoundException
import com.github.dockerjava.api.model.{ExposedPort, HostConfig, Ports}
import com.github.dockerjava.core.{DefaultDockerClientConfig, DockerClientImpl}
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.Futures.{interval, timeout}
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.duration.DurationInt

trait AzureStorageOnDocker extends AnyFunSuite with BeforeAndAfterAll {

  private val imageId = "mcr.microsoft.com/azure-storage/azurite:3.33.0"

  private val dockerHost = "unix:///var/run/docker.sock"

  private var dockerClient: DockerClient = _

  private var containerId: String = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    createContainer()
  }

  override protected def afterAll(): Unit = {
    dockerClient.stopContainerCmd(containerId).exec()
    dockerClient.removeContainerCmd(containerId).withRemoveVolumes(true).exec()
    super.afterAll()
  }

  private def createContainer() = {
    val dockerClientConfig = DefaultDockerClientConfig
      .createDefaultConfigBuilder()
      .withDockerHost(dockerHost)
      .withDockerTlsVerify(false)
      .build()

    val client = new ZerodepDockerHttpClient.Builder()
      .dockerHost(dockerClientConfig.getDockerHost)
      .build()

    dockerClient = DockerClientImpl.getInstance(dockerClientConfig, client)

    try {
      dockerClient.inspectImageCmd(imageId).exec
    } catch {
      case _: NotFoundException =>
        dockerClient.pullImageCmd(imageId).exec(new PullImageResultCallback)
    }

    eventually(timeout(5.minutes), interval(15.seconds)) {
      dockerClient.inspectImageCmd(imageId).exec()
    }

    val ports = new Ports()

    Seq(10000, 10001, 10002).foreach { port =>
      ports.bind(ExposedPort.tcp(port), Ports.Binding.bindPort(port))
    }

    val createCmd = dockerClient
      .createContainerCmd(imageId)
      .withHostConfig(new HostConfig().withPortBindings(ports))
      .exec()

    containerId = createCmd.getId
    dockerClient.startContainerCmd(createCmd.getId).exec()
  }

}
