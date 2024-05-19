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

### Can I make a library for Pickle?

Yes, feel free to create and distribute any tools or libraries for Prickle. I
have chosen to release Prickle as a Java library because that is what I need
however the format can be easily adapted for other platforms.

### What libraries are currently available?

Currently just this one. If any more libraries are created I will update this
section to include them.