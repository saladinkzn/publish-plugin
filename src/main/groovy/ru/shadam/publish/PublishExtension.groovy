package ru.shadam.publish

/**
 * @author sala
 */
class PublishExtension {
  String publicationName = 'mavenJava'

  String name
  String description
  String version

  String projectWebsite
  String projectScm

  String license
  String licenseUrl

  String developerId
  String developerName

  def github(developerId, projectId) {
    this.developerId = developerId
    projectWebsite = "https://github.com/$developerId/$projectId"
    projectScm = "https://github.com/$developerId/$projectId"
  }
}
