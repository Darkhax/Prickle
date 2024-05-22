# Prickle

Prickle is a lightweight configuration format based on JSON. Prickle aims to be
simple, accessible to everyday users, and be very easy to parse and generate
with code.

## Format

### Config Properties

Config properties are wrapped in an object. This allows metadata and other
properties to be associated with the value. The value itself is held by the
`value` key on this object. The format of the value is up to the schema of
the config, it can be a JSON primitive, JSON object, or even another config
property.

**Standard JSON**

```json
{
  "database_host": "192.168.1.0"
}
```

**Prickle Config Property**

```json
{
  "database_host": {
    "value": "192.168.1.0"
  }
}
```

### Comments

The `//` key is used to define a comment on a config property. Comments should
only be used to convey meaning to the user and should have no influence on how
value is parsed or used in code. The value can be a JSON string or an array of
JSON strings for multiline comments.

```json
{
  "database_host": {
    "//": "The IP address of the database to connect to.",
    "value": "192.168.1.0"
  }
}
```

```json
{
  "database_host": {
    "//": [
      "The IP address of the database to connect to.",
      "The port can be suffixed using a colon.      "
    ],
    "value": "192.168.1.0"
  }
}
```

### Decorators

Decorators are a type of comment that exist to convey a specific attribute of
the property to the reader. Decorators are declared using the comment key
followed by an attribute name. For example the `//default` decorator might be
used to denote the default value.

Decorators and their meanings are entirely up to the author of the config
however the following decorators were pretty common during the testing and
development of Prickle.

- `//default` - The default value for the property.
- `//reference` - Points the reader to another resource that can be used to learn more about the property, like a
  wiki/docs page.
- `//range` - A number property that must be within a specific range.
- `//regex` - A string that must match the regex pattern.
- `//empty-allowed` - A collection/array that is/isn't allowed to be empty.

**Examples**

```json
{
  "database_host": {
    "//": [
      "The IP address of the database to connect to.",
      "The port can be suffixed using a colon.      "
    ],
    "//default": "localhost:4321",
    "value": "192.168.1.0"
  }
}
```

```json
{
  "ranged_int": {
    "//": "A number that must fit within a given range.",
    "//range": ">=0 AND <=100",
    "value": 84
  }
}
```

## FaQ

### Why is it called Prickle?

Prickle is the collective noun for a group of hedgehogs. Hedgehogs are one of
my favourite animals and I think they are an oddly fitting metaphor for config
files. For example, both may seem intimidating at first but can be nice once
you get to know and understand them.

### Why not use an existing format?

Before working on Prickle I spent several months test-driving existing formats
and their libraries. While there are several formats that I liked I was
disappointed with a lot of the Java implementations. Every library that I tried
had serious long-standing bugs, was not being maintained, and lacked features
that were important to me. After weighing my options I realized JSON could
easily meet all of my criteria and the tools/libraries to work with JSON are
already widely available.

### Can I make a library for Prickle?

Yes, feel free to create and distribute any tools or libraries for Prickle. I
have chosen to release Prickle as a Java library because that is what I need
however the format can be easily adapted for other platforms.

### What libraries are currently available?

Currently just this one. If any more libraries are created I will update this
section to include them.

## Java Library Guide

### Gradle & Maven

This project is currently hosted on the [BlameJared](https://maven.blamejared.com/net/darkhax/prickle/Prickle/) maven.

```gradle
repositories {
    maven { 
        url 'https://maven.blamejared.com'
    }
}

dependencies {
    implementation group: 'net.darkhax.prickle', name: 'Prickle', version: '1.0.6'
}
```

### Usage Guide

This library uses annotated Java objects to generate a config schema. This 
process is similar to POJO serialization in projects like GSON.

#### Config Classes

The first step in creating a config is writing a class that defines its 
properties. Properties are mapped using the fields of this class and 
annotations on those fields are used to customize how those properties are
managed. When the config is loaded these fields will automatically be updated
with the values that were read from the file.

##### Values
Only the fields marked with the `Value` annotation will be mapped to config
properties. For example this Java class will generate the following config.
```java
public class ExampleConfig {

    @Value
    public String database = "localhost:4321";
    
    public boolean readOnly = true;
}
```
```json
{
  "database": {
    "//default": "localhost:1234",
    "value": "localhost:1234"
  }
}
```

The `Value` annotation has some extra properties that can be used to change how
this library handles the property. 

- `name` - An alternative name used to serialize the property.
- `comment` - A comment for the value. Comments longer than 80 characters will be wrapped into multi-line comments that are padded to that line length.
- `reference` - A link to an online resource that the reader can use to learn more about the value.
- `writeDefault` - The default value will be included by default, but you can disable it with this.

##### Ranged Numbers
You may want to limit numbers to a certain range. This can be done using the 
various ranged number annotations. We currently support `RangedInt`, 
`RangedLong`, `RangedFloat`, and `RangedDouble`. The library will validate the 
value is within the defined range every time the value is loaded.
```java
public class ExampleConfig {

    @Value(name="ranged_int", comment="A number that must fit within a given range.")
    @RangedInt(min=0, max=100)
    public int aRangedInt = 84;
}
```
```json
{
  "ranged_int": {
    "//": "A number that must fit within a given range.",
    "//range": ">=0 AND <=100",
    "//default": 84,
    "value": 84
  }
}
```

##### Regex Strings
String properties can be validated using a regex pattern. This pattern can be 
specified using the `Regex` annotation. The library will validate the value 
matches this pattern every time the value is loaded.

```java
public class ExampleConfig {

    @Value(name="logo_file", comment="The logo file to display.")
    @Regex("^.*\\.(jpg|png)$")
    public String file = "logo.png";
}
```

```json
{
  "logo_file": {
    "//": "The logo file to display.",
    "//regex": "^.*\\.(jpg|png)$",
    "//default": "logo.png",
    "value": "resources/my_logo.png"
  }
}
```

##### Arrays
All arrays and collections will be converted to prickle array properties by 
default. These are mostly identical to JSON arrays but have a few extra
features that can be configured using the `Array` annotation.

Arrays with five or fewer entries that only contain JSON primitives will be 
inlined. This means that the declaration of the array and its values will be
printed on the same line. The threshold can be changed from five using the 
`inlineCount` of the annotation. You can also enable inlining non-primitive 
values by setting `inlineComplex` to true. 

You can also set `allowEmpty` to false which will validate that the array is 
not empty when the value is read. All array properties can be empty by default.

```java
public class ExampleConfig {

    @Value(comment = "An inlined array")
    public int[] intArray = {1, 2, 3, 4};

    @Value(comment = "Not inlined", writeDefault = false)
    @Array(inlineCount = 3)
    public char[] charArray = {'a', 'b', 'c', 'd'};

    @Value(comment = "An array that must not be empty.")
    public List<String> stringList = List.of("first", "second", "third");
}
```

```json
{
  "intArray": {
    "//": "An inlined array",
    "//default": [1,2,3,4],
    "value": [1,2,3,4]
  },
  "charArray": {
    "//": "Not inlined",
    "value": [
      "a",
      "b",
      "c",
      "d"
    ]
  },
  "stringList": {
    "//": "An array that must not be empty.",
    "//default": ["first","second","third"],
    "value": ["first","second","third"]
  }
}
```

#### Reading and Writing
In the previous section you learned how to create a config object class. This
class can be read and written using a ConfigManager. If the file does not exist
it will be generated and saved when you try to load it automatically.

```java
    public static void main(String... args) {
        final ExampleConfig config = new ExampleConfig();
        final ConfigManager<ExampleConfig> manager = new ConfigManager.Builder<ExampleConfig>(Path.of("example.json")).build(config);
        manager.load();
        System.out.println(config.toString());
        manager.save();
    }
```

##### Builder Options
The builder has some helpful options that allow you to further customize your 
config file.

- `logger(Logger)` - Sets the logger used for errors and warnings. If the logger is not specified we will create one using the name of the config object class.
- `adapter(IPropertyAdapter)` - Registers a new property adapter. These let you handle how fields are mapped to config properties. The property can control how the value is serialized.
- `gsonConfig(Consumer<GsonBuilder>)` - Lets you configure the underlying GSON instance.
- `gsonConfig(Function<GsonBuilder, GsonBuilder>)` - Lets you configure the underlying GSON instance, or even replace it.