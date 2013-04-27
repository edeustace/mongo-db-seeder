# Mongo db seeder
A library for seeding your mongo db with json files laid out in a certain way.

# Using in your sbt project
Have a look at the example project, but basically you do the following:

- project/plugins.sbt

    addSbtPlugin("com.ee" % "mongo-db-seeder-sbt" % "0.2-SNAPSHOT")

    resolvers += "ed release repo" at "http://edeustace.com/repository/snapshots"

- build.sbt

    //Import the MongoDbSeeder settings
    MongoDbSeederPlugin.newSettings

    //Specify a mongo uri for the test environment
    testUri := "light"

    //Specify some paths that contain seed data for test
    testPaths := "seed/test,seed/common"

    //Specify some paths that contain seed data for dev
    devPaths := "seed/common,seed/dev"

    //Tip: always seed the db before testing:
    (test in Test) <<= (test in Test) dependsOn(seedTestTask)

## Seed data format
The seed data files can be laid out using any of the following conventions:

#### Single file where each line is a document in a collection and the file name matches a collection name.

eg: content.json  => will insert each line of the file into the 'content' collection

#### Single file whose name is list.json and whose contents is a json array

eg: content/list.json  => will insert each item in the array into the 'content' collection

#### Folder that contains x number of json files (and doesn't contain 'list.json' + isn't a single file)

eg: content/*.json => will insert each json file into the 'content' collection.



# Developing

## Requirements

- sbt 12
- scala 2.9.2



## Running

    sbt

## Modules
The project is split into 2 modules:

### lib
This contains the seed logic

### sbt
This hooks up that logic to a set of sbt tasks


## Example
Is under the folder 'example', just cd into it and run sbt
