# mongo-db-seeder

A little library that seeds a mongo database based on files laid out in a given convention:

### Single file where each line is a document in a collection and the file name matches a collection name.

eg: content.json  => will insert each line of the file into the 'content' collection

### Single file whose name is list.json and whose contents is a json array

eg: content/list.json  => will insert each item in the array into the 'content' collection

### Folder that contains x number of json files (and doesn't contain 'list.json' + isn't a single file)

eg: content/*.json => will insert each json file into the 'content' collection.

## Usage
    
    com.ee.seeder.MongoDbSeeder.seed(uri, paths)

Will add the json content at the given paths using the uri and the file layout as specified above.


    com.ee.seeder.MongoDbSeeder.emptyDb(uri,paths)

Will empty all collections derived from the json files using the layout above.


## Developing

* install sbt
* to test: `sbt test` - you'll need a local mongo db installed
* to build: `sbt package`