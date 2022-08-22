# Transformations Between VDM and UML

## Contents



## Basic Data Types
Basic data types are included in PlantUML as the type of a class attribute.


## Compound Types

### Multiplicity Types
Non associative multiplicity types are included as the type of a class attribute.

#### set:
non associative: set of element
associative: class -> "*" object

#### seq:
non associative: seq of element
associative: class -> "(*)" object

#### seq1:
non associative: seq1 of element
associative: class "1" -> "(*)" object

### Qualified Association Types

#### map:
non associative: "mapName" map qualifier to multiplicity element
associative: class "[qualifier]" -> multiplicity object

#### inmap:
non associative: "mapName" map qualifier to multiplicity element
associative: class "[(qualifier)]" -> multiplicity object

### Other Compound Types

#### optinal type: "[*]"
#### composite type: "::" 
#### union type: "*|*"
#### product type: "*"


