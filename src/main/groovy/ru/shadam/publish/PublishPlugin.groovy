package ru.shadam.publish

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.publish.maven.MavenArtifact
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
/**
 * @author sala
 */
class PublishPlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    project.apply(plugin: 'maven-publish')

    if(!project.tasks.findByName('classes')) {
      throw new GradleException("Non-JVM projects are not supported yet")
    }

    if(!project.tasks.findByName('sourcesJar')) {
      project.task('sourcesJar', type: Jar, dependsOn: project.classes, description: 'Creates sources jar') {
        classifier = 'sources'
        from project.sourceSets.main.allSource
      }
    }

    if(!project.tasks.findByName('javadocJar')) {
      project.task('javadocJar', type: Jar, description: 'Creates javadoc jar') {
        classifier = 'javadoc'
        dependsOn(project.tasks.javadoc)
      }
    }

    def extension = project.extensions.create('publish', PublishExtension).with {
      initExtension(it, project)
      it
    }

    project.afterEvaluate { Project it ->
      def errors = validateExtension(extension)

      if (errors) {
        throw new GradleException("Failed to validate extension: ${errors.join(', ')}")
      }

      def pc = it.publishing.publications
      MavenPublication p = pc.create(extension.publicationName, MavenPublication)
      p.pom.withXml { XmlProvider xp ->
        xp.asNode().with {
          appendNode('name', extension.name)
          appendNode('description', extension.description)
          appendNode('url', extension.projectWebsite)
          appendNode('scm').with {
            appendNode('url', extension.projectScm)
            appendNode('connection', extension.projectScm)
            appendNode('developerConnection', extension.projectScm)
          }
          appendNode('licenses').with {
            appendNode('license').with {
              appendNode('name', extension.license)
              appendNode('url', extension.licenseUrl)
              appendNode('distribution', 'repo')
            }
          }
          appendNode('developers').with {
            appendNode('developer').with {
              appendNode('id', extension.developerId)
              appendNode('name', extension.developerName)
            }
          }
        }
      }

      p.from(it.components.getByName("java"))
//
      p.artifact(it.tasks.sourcesJar) { MavenArtifact ma ->
        ma.classifier = 'sources'
      }

      p.artifact(it.tasks.javadocJar) { MavenArtifact ma ->
        ma.classifier = 'javadoc'
      }
    }
  }

  private static def validateExtension(PublishExtension extension) {
    def errors = []
    if(!extension.name) errors.add('name')
    if(!extension.description) errors.add('description')
    if(!extension.version) errors.add('version')
    if(!extension.projectScm) errors.add('projectScm')
    if(!extension.projectWebsite) errors.add('projectWebsite')
    if(!extension.developerId) errors.add('developerId')
    if(!extension.developerName) errors.add('developerName')
    if(!extension.license) errors.add('license')
    if(!extension.licenseUrl) errors.add('licenseUrl')
    errors
  }

  private static void initExtension(PublishExtension extension, Project project) {
    extension.name = project.name
    extension.version = project.version
  }

}
