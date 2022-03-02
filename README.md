# BukkitGetter

A simple Ktor-based HTTP server to automatically get the latest spigot build. It does this by running BuildTools on an hourly basis.

## Endpoints
### `GET` /
Returns the current API version.

### `GET` /spigot
Returns the default spigot jar.

### `GET` /spigot/api
Returns the spigot api jar.

### `GET` /spigot/server
Returns the spigot server jar.

## Setting up
### Building yourself
```shell
# Build BukkitGetter
$ mvn package

# Copy to a place of your liking
$ cp target/bukkitgetter-1.0-jar-with-dependencies.jar $LOCATION

# Run 
$ java -jar bukkitgetter-1.0-jar-with-dependencies.jar
```

### GitHub Releases
1. Download the latest (pre)release from the [releases]().
2. Drag the file in a directory of your choice.
3. Open a terminal/command prompt in that directory and run the following command.
```shell
$ java -jar bukkitgetter-1.0.jar
```

### Docker
*Coming soon.*