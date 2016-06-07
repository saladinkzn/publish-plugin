**Publish-plugin** is a simple gradle plugin which can be used to reduce boilerplate when using maven-publishing plugin.

#### Usage

```groovy
buildscript {
  repositories {
    jcenter()
  }
  
  dependencies {
    classpath 'ru.shadam:publish-plugin:0.1.0'
  }
}

publish {
  // default is mavenJava
  publicationName = 'pluginMaven'

  description = 'Lorem ipsum dolorem'
  github 'johndoe', 'example'
  developerName = 'John Doe'
  license = 'MIT'
  licenseUrl = 'https://example.org/license'
}
```

#### Copyright and License

Licensed under [MIT license](LICENSE).



