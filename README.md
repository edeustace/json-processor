#json-processor
This library is a utility to allow you to map from one flavour of json to another.

eg if you have: 

    [
      { 
        "fruit" : "Apple" 
      }
    ]

You can declare a map like so:

    {
      "fruit" : "favouriteFruits"
    }

Running this will generate: 

    [
     {
       "favouriteFruits" : "Apple"
     }
    ]

### Options and commands

####!ignore

suppresses that item from the output

Eg: `[{"name":"Ed", "number" : 1234 }]` + `{"number":"!ignore"}` = `[{"name":"Ed"}]`

#### !camelCase 

convert the existing key to camelCase

Eg: `[{"First Name":"Ed"}]` + `{"First Name":"!camelCase"}` = `[{"firstName":"Ed"}]`

#### !merge-> 

merge a couple of the keys into one keyed value. With merge you have to specify a new key name and also provide a template for that new key. So if you add “!merge->newKey”, “newKey” will be the name of the new keyed item, which will use a template called “newKey” that adds the other items into it.


The input:

    [{
        "firstName" : "Ed",
        "lastName" : "Eustace"
    }]

The map: 

    {
        "firstName" : "!merge->fullName", 
        "lastName" : "!merge->fullName", 
        "fullname": "${firstName} ${lastName}" 
    }

The output:

     [{
        "fullName":"Ed Eustace"
    }]

#### !insert

insert key/value into output.

Eg: `[{"name":"Ed" }]` + `{"!insert:type":"human"}` = `[{"name":"Ed", "type":"human"}]`



## Running

    java jar json-processor-fat.jar myJson.json map.json output.json
    
## Limitations

- It only maps lists at the moment.

- It only maps one object to one object - aka you can't merge from multiple objects to one

## Developing
    git clone repo
    brew install sbt
    cd json-processor
    sbt assembly

