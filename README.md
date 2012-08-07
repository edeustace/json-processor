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

- !ignore
- !camelCase
- !merge->


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

