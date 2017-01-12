import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class SimpleSpecification extends Specification {
  @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
  File buildFile
  BuildResult result

  def setup() {
    buildFile = testProjectDir.newFile('build.gradle')
  }

  def "should fail without configuration"() {
    given:
    buildFile << """
      plugins {
        id 'java'
        id 'ru.shadam.publish-plugin'
      }
    """

    when:
    result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("tasks")
            .withPluginClasspath()
            .buildAndFail()

    then:
    Assert.assertNotNull(result)
  }

  def "should not fail with configuration"() {
    given:
    buildFile << """
      plugins {
        id 'java'
        id 'ru.shadam.publish-plugin'
      }

      publish {
        description = 'Lorem ipsum dolorem'
        projectScm = 'https://example.org/example.git'
        projectWebsite = 'https://example.org'
        developerId = 'johndoe'
        developerName = 'John Doe'
        license = 'MIT'
        licenseUrl = 'https://example.org/license'
      }
    """

    when:
    result = GradleRunner.create()
              .withProjectDir(testProjectDir.root)
              .withArguments("generatePomFileForMavenJavaPublication")
              .withPluginClasspath()
              .build()

    then:
    def build = new File(testProjectDir.root, "build")
    def publications = new File(build, 'publications')
    def mavenJava = new File(publications, 'mavenJava')
    def pom = new File(mavenJava, 'pom-default.xml')
    Assert.assertTrue(pom.exists())
    def pomText = new XmlParser().parseText(pom.getText())
    Assert.assertEquals('Lorem ipsum dolorem', pomText.description.text())
    Assert.assertEquals('https://example.org', pomText.url.text())
    Assert.assertEquals('https://example.org/example.git', pomText.scm.url.text())
    Assert.assertEquals('https://example.org/example.git', pomText.scm.connection.text())
    Assert.assertEquals('https://example.org/example.git', pomText.scm.developerConnection.text())
    Assert.assertEquals('MIT', pomText.licenses.license.name.text())
    Assert.assertEquals('https://example.org/license', pomText.licenses.license.url.text())
    Assert.assertEquals('johndoe', pomText.developers.developer.id.text())
    Assert.assertEquals('John Doe', pomText.developers.developer.name.text())
  }

  def "github method is working"() {
    given:
    buildFile << """
      plugins {
        id 'java'
        id 'ru.shadam.publish-plugin'
      }

      publish {
        description = 'Lorem ipsum dolorem'
        github 'johndoe', 'example'
        developerName = 'John Doe'
        license = 'MIT'
        licenseUrl = 'https://example.org/license'
      }
    """

    when:
    result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("generatePomFileForMavenJavaPublication")
            .withPluginClasspath()
            .build()

    then:
    def build = new File(testProjectDir.root, "build")
    def publications = new File(build, 'publications')
    def mavenJava = new File(publications, 'mavenJava')
    def pom = new File(mavenJava, 'pom-default.xml')
    Assert.assertTrue(pom.exists())
    def pomText = new XmlParser().parseText(pom.getText())
    Assert.assertEquals('Lorem ipsum dolorem', pomText.description.text())
    Assert.assertEquals('https://github.com/johndoe/example', pomText.url.text())
    Assert.assertEquals('https://github.com/johndoe/example', pomText.scm.url.text())
    Assert.assertEquals('https://github.com/johndoe/example', pomText.scm.connection.text())
    Assert.assertEquals('https://github.com/johndoe/example', pomText.scm.developerConnection.text())
    Assert.assertEquals('MIT', pomText.licenses.license.name.text())
    Assert.assertEquals('https://example.org/license', pomText.licenses.license.url.text())
    Assert.assertEquals('johndoe', pomText.developers.developer.id.text())
    Assert.assertEquals('John Doe', pomText.developers.developer.name.text())
  }

  def "publication name property should work"() {
    given:
    buildFile << """
      plugins {
        id 'java'
        id 'ru.shadam.publish-plugin'
      }

      publish {
        publicationName = 'pluginMaven'

        description = 'Lorem ipsum dolorem'
        github 'johndoe', 'example'
        developerName = 'John Doe'
        license = 'MIT'
        licenseUrl = 'https://example.org/license'
      }
    """

    when:
    result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("generatePomFileForPluginMavenPublication")
            .withPluginClasspath()
            .build()

    then:
    def build = new File(testProjectDir.root, "build")
    def publications = new File(build, 'publications')
    def mavenJava = new File(publications, 'pluginMaven')
    def pom = new File(mavenJava, 'pom-default.xml')
    Assert.assertTrue(pom.exists())
  }
}
